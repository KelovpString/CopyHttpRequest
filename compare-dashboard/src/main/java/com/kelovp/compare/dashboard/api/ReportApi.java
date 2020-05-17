package com.kelovp.compare.dashboard.api;

import com.alibaba.fastjson.JSON;
import com.kelovp.compare.dashboard.service.CompareApiService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

/**
 * @author KelovpString
 */
@RestController
@RequestMapping("report")
@ResponseBody
@Slf4j
public class ReportApi {

    @Autowired
    private CompareApiService compareApiService;

    @GetMapping(value = "/queryApplication",produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public String queryAllRes(String applicationName){
        return JSON.toJSONString(compareApiService.queryRes(applicationName));
    }

    @GetMapping(value = "/queryExpFuncCount",produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public String queryExpFuncCount(String applicationName){
        return compareApiService.queryExpFuncCount(applicationName);
    }

    @GetMapping(value = "/queryExpFuncLimit",produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public String queryExpFuncLimit(String applicationName){
        log.info("queryExpFuncLimit--{}",applicationName);
        return compareApiService.queryExpFuncLimit(applicationName);
    }

    @GetMapping(value = "/queryAll",produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public String queryAll(){
        return compareApiService.loadAllRes();
    }

    @GetMapping(value = "/queryAllFuc",produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public String queryAllFuc(String applicationName){
        return compareApiService.queryAllFunction(applicationName);
    }

    @GetMapping(value = "/queryTimeApp",produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public String queryTimeApp(String applicationName){
        return compareApiService.queryTimeApp(applicationName);
    }


    @PostMapping(value = "/queryString",produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public String queryResult(@RequestBody String queryString){
        log.info(queryString);
        return compareApiService.queryResult(queryString);
    }
}
