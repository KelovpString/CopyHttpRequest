package com.kelovp.compare.core.conf;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @author KelovpString
 */
@Configuration
@ConfigurationProperties(prefix = "com.kelovp.compare")
@Getter
@Setter
public class CompareApiConf {

    /**
     * 需要比对的方法名称 可动态配置 由,分割
     */
    private String needCompareFunction;

    /**
     * 需要复制的机器
     */
    private String targetHost;

    /**
     * 允许开启复制的机器
     */
    private String sourceHost;

    /**
     * 是否记录Mango 默认否
     */
    private boolean mangoConf = false;

    /**
     * 是否添加日志
     */
    private boolean printLog = true;

    /**
     * 比对开关，默认关
     */
    private boolean enable = false;

    /**
     * mango 机器地址
     */
    private String dashboardHost;

    /**
     * 参与比对的应用 可选多个，对应spring applicationName
     */
    private String joinApplication;
}
