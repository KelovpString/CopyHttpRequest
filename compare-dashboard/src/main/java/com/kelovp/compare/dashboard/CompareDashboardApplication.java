package com.kelovp.compare.dashboard;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.mongo.MongoDataAutoConfiguration;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;

@SpringBootApplication(exclude = {MongoDataAutoConfiguration.class, MongoAutoConfiguration.class})
public class CompareDashboardApplication {

    public static void main(String[] args) {
        SpringApplication.run(CompareDashboardApplication.class, args);
    }

}
