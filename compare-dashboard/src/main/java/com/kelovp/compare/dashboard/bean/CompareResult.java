package com.kelovp.compare.dashboard.bean;

import com.alibaba.fastjson.JSONObject;
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
}
