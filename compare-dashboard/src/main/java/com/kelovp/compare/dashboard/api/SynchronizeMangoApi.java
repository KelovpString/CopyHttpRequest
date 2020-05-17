package com.kelovp.compare.dashboard.api;

import com.kelovp.compare.dashboard.bean.CompareResult;
import com.kelovp.compare.dashboard.service.CompareApiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

/**
 * @author KelovpString
 */
@RestController
@RequestMapping("service")
public class SynchronizeMangoApi {


    @Autowired
    private CompareApiService compareApiService;

    @PostMapping(value = "/synchRst",produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public void synchronizeResult(@RequestBody CompareResult compareResult){
        compareApiService.save(compareResult);
    }

    @DeleteMapping(value = "/delApplication",produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public void delCollection(String applicationName){
        compareApiService.removeCollection(applicationName);
    }
}
