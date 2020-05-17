package com.kelovp.compare.core.conf;

import lombok.extern.slf4j.Slf4j;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;

/**
 * @author KelovpString
 */
@Configuration
@Slf4j
public class HttpClientConf {

    private PoolingHttpClientConnectionManager poolingHttpClientConnectionManager(){
        PoolingHttpClientConnectionManager manager = new PoolingHttpClientConnectionManager();
        manager.setMaxTotal(10);
        manager.setDefaultMaxPerRoute(50);
        return manager;
    }

    private HttpClientBuilder httpClientBuilder(){
        HttpClientBuilder builder = HttpClientBuilder.create();
        builder.setConnectionManager(poolingHttpClientConnectionManager());
        return builder;
    }

    @Bean(name = "clientHttpRequestFactory")
    public HttpComponentsClientHttpRequestFactory httpComponentsClientHttpRequestFactory(){
        HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory(httpClientBuilder().build());
        factory.setConnectTimeout(30000);
        factory.setReadTimeout(30000);
        log.info("clientHttpRequestFactory 已创建");
        return factory;
    }
}
