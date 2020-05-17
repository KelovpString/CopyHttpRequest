package com.kelovp.compare.core.bean;

import com.alibaba.fastjson.JSONObject;
import com.kelovp.compare.core.util.CompareJsonUtils;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

/**
 * @author KelovpString
 */
@Getter
@Setter
public class CompareResult {
    private Object onlineRst;
    private JSONObject grayRst;
    private boolean isSame = false;
    private Date createTime;
    private Date updateTime;
    private String name;
    private String reqMethod;
    private String copyUrl;
    private String sourceStr;
    private String copyStr;
    private String applicationName;

    public CompareResult(JSONObject onlineRst, JSONObject grayRst, String[] excludFields) {
        isSame = CompareJsonUtils.compareTwoJsonObject(onlineRst, grayRst, this, excludFields);
        this.onlineRst = onlineRst;
        this.grayRst = grayRst;
        this.createTime = new Date();
        this.updateTime = new Date();
    }

    public CompareResult() {
    }

    public CompareResult(Object onlineRst, JSONObject grayRst, boolean isSame, Date createTime, Date updateTime, String name, String reqMethod, String copyUrl, String sourceStr, String copyStr) {
        this.onlineRst = onlineRst;
        this.grayRst = grayRst;
        this.isSame = isSame;
        this.createTime = createTime;
        this.updateTime = updateTime;
        this.name = name;
        this.reqMethod = reqMethod;
        this.copyUrl = copyUrl;
        this.sourceStr = sourceStr;
        this.copyStr = copyStr;
    }
}
