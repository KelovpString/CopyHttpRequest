package com.kelovp.compare.core.conf;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.ThreadPoolExecutor;

/**
 * @author KelovpString
 */
@Configuration
@Slf4j
public class ThreadPoolConf {

    @Bean(name = "futureTaskPool")
    public ThreadPoolTaskExecutor productPoolConf(){
        ThreadPoolTaskExecutor pool = new ThreadPoolTaskExecutor();
        //核心线程数目
        pool.setCorePoolSize(16);
        //指定最大线程数
        pool.setMaxPoolSize(64);
        //队列中最大的数目
        pool.setQueueCapacity(16);
        //线程名称前缀
        pool.setThreadNamePrefix("futureTaskPool_");
        //rejection-policy：当pool已经达到max size的时候，如何处理新任务
        // useDiscard 直接抛弃
        pool.setRejectedExecutionHandler(new ThreadPoolExecutor.DiscardPolicy());
        //线程空闲后的最大存活时间
        pool.setKeepAliveSeconds(30);
        pool.initialize();
        log.info("futureTaskPool 已初始化");
        return pool;
    }

}

