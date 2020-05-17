package com.kelovp.compare.core.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.util.URIUtil;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.client.ClientHttpRequest;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Map;

/**
 * @author KelovpString
 */
@Component
@Slf4j
public class HttpClientService {

    @Resource(name = "clientHttpRequestFactory")
    ClientHttpRequestFactory clientHttpRequestFactory;

    public String transit(URI uri) throws IOException {
        ClientHttpRequest httpclient = clientHttpRequestFactory.createRequest(uri, HttpMethod.GET);
        ClientHttpResponse response = httpclient.execute();
        BufferedReader read = new BufferedReader(new InputStreamReader(response.getBody(), StandardCharsets.UTF_8));
        String line = null;
        StringBuilder sb = new StringBuilder();
        while ((line = read.readLine()) != null) {
            sb.append(line);
        }

        read.close();

        log.debug(sb.toString());
        return sb.toString();
    }

    public String sendReq(String requrl, Map<String,String> headers,boolean printLog) throws IOException {
        int windex = requrl.indexOf("?");
        if (windex > 0) {
            String parstr = requrl.substring(windex + 1, requrl.length());
            String qstr = URIUtil.encodeQuery(parstr);

            requrl = requrl.substring(0, windex + 1) + qstr;
        }
        if (printLog) {
            log.info("Request URL: " + requrl);
        }
        URI uri = URI.create(requrl);
        long curTime = System.currentTimeMillis();
        ClientHttpRequest httpclient = clientHttpRequestFactory.createRequest(uri, HttpMethod.GET);
        if(headers != null && headers.size()>0){
            for (String key : headers.keySet()) {
                httpclient.getHeaders().add(key, headers.get(key));
            }
        }

        ClientHttpResponse response = httpclient.execute();
        BufferedReader read = new BufferedReader(new InputStreamReader(response.getBody(), StandardCharsets.UTF_8));
        String line = null;
        StringBuilder sb = new StringBuilder();
        while ((line = read.readLine()) != null) {
            sb.append(line);
        }

        read.close();

        if (printLog) {
            log.info("cost [" + (System.currentTimeMillis() - curTime) + "] req  [" + requrl + "] rsp is [" + sb + "]");
        }
        return sb.toString();
    }

    public String sendReqPost(String requrl,Map<String,String> headers,boolean printLog) throws IOException {
        int windex = requrl.indexOf("?");
        if (windex > 0) {
            String parstr = requrl.substring(windex + 1, requrl.length());
            String qstr = URIUtil.encodeQuery(parstr);

            requrl = requrl.substring(0, windex + 1) + qstr;
        }
        if (printLog) {
            log.info("Request URL: " + requrl);
        }
        URI uri = URI.create(requrl);
        long curtime = System.currentTimeMillis();
        ClientHttpRequest httpclient = clientHttpRequestFactory.createRequest(uri, HttpMethod.POST);
        if(headers != null && headers.size()>0){
            for (String key : headers.keySet()) {
                httpclient.getHeaders().add(key, headers.get(key));
            }
        }
        ClientHttpResponse response = httpclient.execute();
        StringBuilder sb = new StringBuilder();
        BufferedReader read = null;
        try {
            read = new BufferedReader(new InputStreamReader(response.getBody(), StandardCharsets.UTF_8));
            String line = null;
            while ((line = read.readLine()) != null) {
                sb.append(line);
            }
        } catch (Exception e) {
            log.error("", e);
        } finally {
            assert read != null;
            read.close();
        }

        if (printLog) {
            log.info("cost [" + (System.currentTimeMillis() - curtime) + "] req  [" + requrl + "] rsp is [" + sb + "]");
        }
        return sb.toString();
    }

    public String postJSON(String requrl, String jsonBody,Map<String,String> headers,boolean printLog) throws IOException {
        int windex = requrl.indexOf("?");
        if (windex > 0) {
            String parstr = requrl.substring(windex + 1, requrl.length());
            String qstr = URIUtil.encodeQuery(parstr);

            requrl = requrl.substring(0, windex + 1) + qstr;
        }
        if (printLog) {
            log.info("Request URL: " + requrl + ", RequestBody:" + jsonBody);
        }
        if (jsonBody != null && !"".equals(jsonBody.trim())) {
            try {
                HttpPost method = new HttpPost(requrl);
                if(headers != null && headers.size()>0){
                    for (String key : headers.keySet()) {
                        method.addHeader(key, headers.get(key));
                    }
                }
                method.addHeader("Content-type", "application/json;charset=UTF-8");

                method.setEntity(new StringEntity(jsonBody, StandardCharsets.UTF_8));
                HttpClient httpClient = getHttpClient();
                HttpResponse response = httpClient.execute(method);
                int statusCode = response.getStatusLine().getStatusCode();
                String result = EntityUtils.toString(response.getEntity(), "UTF-8");
                if (printLog) {
                    log.info("Request URL: " + requrl + ", RequestBody:" + jsonBody + ",Response:" + result);
                }
                if (statusCode == HttpStatus.SC_OK || statusCode == HttpStatus.SC_CREATED ) {
                    return result;
                }
            } catch (IOException e) {
                log.error("", e);
            }
        }
        return null;
    }

    private HttpClient getHttpClient() {
        HttpClient httpClient = new DefaultHttpClient();
        httpClient.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 5000);
        httpClient.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, 30000);
        return httpClient;
    }

}
