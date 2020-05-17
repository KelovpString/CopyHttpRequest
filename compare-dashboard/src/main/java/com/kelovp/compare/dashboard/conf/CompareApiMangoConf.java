package com.kelovp.compare.dashboard.conf;


import com.mongodb.MongoClient;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.MongoDbFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoDbFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * @author KelovpString
 * @Date 2020/3/12 15:01
 */
@Configuration
@ConditionalOnBean(value = {CompareApiMangoProperties.class})
@Slf4j
public class CompareApiMangoConf {

    @Autowired
    private CompareApiMangoProperties mongoProperties;

    @Bean(name = "apiCompareMongoTemplate")
    public MongoTemplate primaryMongoTemplate() throws Exception {
        return new MongoTemplate(mongodbApicmpFactory(mongoProperties));
    }

    public MongoDbFactory mongodbApicmpFactory(CompareApiMangoProperties mongoProperties) throws Exception {
        ServerAddress serverAddress = new ServerAddress(mongoProperties.getHost(), mongoProperties.getPort());
        List<MongoCredential> credentialsList = new ArrayList<MongoCredential>();
        credentialsList.add(MongoCredential.createCredential(mongoProperties.getUsername(), mongoProperties.getDatabase(), mongoProperties.getPassword().toCharArray()));
        return new SimpleMongoDbFactory(new MongoClient(serverAddress, credentialsList), mongoProperties.getDatabase());
    }
}
