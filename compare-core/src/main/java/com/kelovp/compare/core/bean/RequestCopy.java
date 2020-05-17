package com.kelovp.compare.core.bean;

import lombok.Getter;
import lombok.Setter;

import java.util.Map;

/**
 * @author KelovpString
 */
@Setter
@Getter
public class RequestCopy {
    private String rp;

    private String requestMethod;

    private String uri;

    private String requestBody;

    private String requestParam;

    private String userHeader;

    private Map<String,String> requestHeaders;
}
