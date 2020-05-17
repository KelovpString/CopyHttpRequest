package com.kelovp.compare.dashboard.conf;


import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @author KelovpString
 */
@Configuration
@ConfigurationProperties(prefix = "api.compare.mongodb")
@Getter
@Setter
public class CompareApiMangoProperties {
    private String host;
    private String database;
    private String username;
    private String password;
    private int port;
}
