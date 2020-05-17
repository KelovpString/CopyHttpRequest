package com.kelovp.compare.core.aop;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializeConfig;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.alibaba.fastjson.serializer.SimpleDateFormatSerializer;
import com.kelovp.compare.core.anno.CompareApi;
import com.kelovp.compare.core.bean.CompareResult;
import com.kelovp.compare.core.bean.RequestCopy;
import com.kelovp.compare.core.conf.CompareApiConf;
import com.kelovp.compare.core.service.HttpClientService;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.util.ContentCachingRequestWrapper;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.InetAddress;
import java.util.*;
import java.util.concurrent.*;

/**
 * @author KelovpString
 */
@Component
@Aspect
@Slf4j
public class ApiCompareInterceptor {

    @Autowired
    private HttpClientService httpClientService;
    @Autowired
    private CompareApiConf compareApiConf;
    @Value("${com.kelovp.application.name}")
    private String applicationName;

    @Resource(name = "futureTaskPool")
    private ThreadPoolTaskExecutor executor;


    @Around(value = "@annotation(com.kelovp.compare.core.anno.CompareApi)", argNames = "joinPoint")
    public Object copyAndCompare(ProceedingJoinPoint joinPoint) throws Throwable {
        HttpServletRequest request = initRequest();
        Object proceed = joinPoint.proceed();
        try{
            RequestCopy requestCopy = invokeRequest(request);
            CompletableFuture.runAsync(() ->{
                try {
                    if (!compareApiConf.isEnable() || !filterSourceHost() || !filterApplication()) {
                        return;
                    }
                    copyRequest(joinPoint,proceed,requestCopy);
                }catch (Exception e){
                    log.error("CopyRequestError:",e);
                }
            },executor);
        }catch (Exception e){
            log.error("Copy NewRequestError:",e);
        }
        return proceed;
    }

    private boolean filterSourceHost(){
        try {
            String localIp =InetAddress.getLocalHost().getHostAddress();
            return compareApiConf.getSourceHost().equals(localIp);
        } catch (Exception e) {
          log.error("GET HOST EX:",e);
        }
        return false;
    }

    private boolean filterApplication(){
        try {
            List<String> applicationList = Arrays.asList(compareApiConf.getJoinApplication().split(","));
            return applicationList.contains(applicationName);
        } catch (Exception e) {
            log.error("GET HOST EX:",e);
        }
        return false;
    }

    private void copyRequest(ProceedingJoinPoint joinPoint,Object finalObj,RequestCopy copy) throws IOException {
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        Method method = methodSignature.getMethod();
        CompareApi compApi = method.getAnnotation(CompareApi.class);
        if (finalObj == null) {
            return;
        }

        if (StringUtils.isEmpty(compareApiConf.getNeedCompareFunction()) ||
                StringUtils.isEmpty(compareApiConf.getTargetHost())){
            log.error("CompareApi CONF Ex");
            return;
        }

        List<String> functionList = Arrays.asList(compareApiConf.getNeedCompareFunction().split(","));
        if (!functionList.contains(compApi.name())){
            return;
        }
        SerializeConfig mapping = new SerializeConfig();
        mapping.put(Date.class, new SimpleDateFormatSerializer("yyyy-MM-dd HH:mm:ss"));
        String onlineRstStr = JSONObject.toJSONString(finalObj, mapping, SerializerFeature.WriteDateUseDateFormat);
        JSONObject onlineJsonObj = JSONObject.parseObject(onlineRstStr);

        JSONObject res =  null;
        if (StringUtils.isEmpty(copy.getRp()) && compareApiConf != null && copy.getUri()!= null) {
            String reqUrl = compareApiConf.getTargetHost() + copy.getUri();
            if (!StringUtils.isEmpty(copy.getRequestParam())){
                reqUrl = reqUrl + "?" + copy.getRequestParam();
            }else {
                reqUrl = reqUrl + "?key=nx";
            }

            Map<String, String> header = new HashMap<>();
            header.put("userHeader",copy.getUserHeader());
            if ("get".equals(copy.getRequestMethod().toLowerCase())) {
                String html = httpClientService.sendReq(reqUrl + "&rp=test", header,compareApiConf.isPrintLog());
                res = JSON.parseObject(html);
            } else if ("post".equals(copy.getRequestMethod().toLowerCase())) {
                String html = null;
                if (StringUtils.isEmpty(copy.getRequestBody())) {
                    html = httpClientService.sendReqPost(reqUrl + "&rp=test", header,compareApiConf.isPrintLog());
                }else {
                    html = httpClientService.postJSON(reqUrl + "&rp=test",copy.getRequestBody(), header,compareApiConf.isPrintLog());
                }
                res = JSON.parseObject(html);
            }

            if (res != null){
                solveResponse(compApi,res,onlineJsonObj,copy.getRequestMethod(),reqUrl);
            }
        }
    }

    private void solveResponse(CompareApi compApi,JSONObject res,JSONObject onlineJsonObj,String requestMethod,String reqUrl){
        CompareResult compareRst = new CompareResult(onlineJsonObj, res, compApi.excludeFields());
        compareRst.setName(compApi.name());
        compareRst.setReqMethod(requestMethod);
        compareRst.setCopyUrl(reqUrl);
        if (compareApiConf.isPrintLog()){
            log.info("--CompareAPI RESULT:[{}]-[{}]",compareRst.getName(),compareRst.isSame());
            if (!compareRst.isSame()){
                log.info("--CompareAPI ONLine [{}] RESULT {}",compareRst.getName(),compareRst.getSourceStr());
                log.info("--CompareAPI COPY [{}] RESULT {}",compareRst.getName(),compareRst.getCopyStr());
            }
        }
        if (compareApiConf.isMangoConf()) {
            compareRst.setApplicationName(applicationName);
            try {
                httpClientService.postJSON(compareApiConf.getDashboardHost() + "/service/synchRst",
                        JSON.toJSONString(compareRst),null,compareApiConf.isPrintLog());
            }catch (Exception e){
                log.error("Synch EX:",e);
            }

        }
    }

    private static RequestCopy invokeRequest(HttpServletRequest request){
        try {
            if (request == null) {
                return null;
            }
            ContentCachingRequestWrapper wrapper = new ContentCachingRequestWrapper(request);
            String rp = wrapper.getParameter("rp");
            String requestMethod = wrapper.getMethod();
            String uri = wrapper.getRequestURI();
            String param = readParam(wrapper);
            String body = readBody(wrapper);
            String userHeader = wrapper.getHeader("userHeader");

            return new RequestCopy() {{
                setRp(rp);
                setRequestMethod(requestMethod);
                setUri(uri);
                setRequestParam(param);
                setRequestBody(body);
                setUserHeader(userHeader);
            }};
        }catch (Exception e){
            log.error("Solve Request Error:",e);
        }
        return null;
    }

    private static String readParam(ContentCachingRequestWrapper request) {
        StringBuilder sb = new StringBuilder();
        try{
            Map<String, String[]> keyMap = request.getParameterMap();
            if (keyMap == null){
                return sb.toString();
            }
            Set<String> keySet = keyMap.keySet();
            if (keySet.size() > 0) {
                for (String key : keySet) {
                    String[] values = request.getParameterValues(key);
                    if (values != null && values.length > 0) {
                        for (String value : values) {
                            sb.append(key).append("=").append(value).append("&");
                        }
                    }
                }
            }
        }catch(Exception e){
            log.error("", e);
        }
        return sb.toString();
    }

    private HttpServletRequest initRequest() {
        try {
            RequestAttributes attributes = RequestContextHolder.currentRequestAttributes();
            return ((ServletRequestAttributes) Objects.requireNonNull(attributes)).getRequest();
        } catch (Exception e) {
            log.error("", e);
        }
        return null;
    }

    public static String readBody(ContentCachingRequestWrapper request) {
        try {
            /**
             * 但是为了Jar包不依赖其他其实应该创建ContentCaching的RequestFilter
             *  @see com.kelovp.compare.core.conf.FilterConf
             */
            // 由于上层包装了一次
            ContentCachingRequestWrapper sec = (ContentCachingRequestWrapper)request.getRequest();
            return new String(sec.getContentAsByteArray());
        }catch (Exception e){
            log.error("CopyBody EX:",e);
        }
        return null;
    }

}
