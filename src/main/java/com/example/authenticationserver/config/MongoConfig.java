package com.example.authenticationserver.config;
import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.data.mongodb.config.AbstractMongoClientConfiguration;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

import java.util.Collection;
import java.util.Collections;
import java.util.Objects;

@Configuration
public class MongoConfig extends AbstractMongoClientConfiguration {
    @Autowired
    Environment env;
    @Override
    protected String getDatabaseName() {
        return "paint";
    }

    @Override
    public MongoClient mongoClient() {
        ConnectionString connectionString = new ConnectionString(Objects.requireNonNull(env.getProperty("spring.data.mongodb.uri")));
        MongoClientSettings mongoClientSettings = MongoClientSettings.builder()
                .applyConnectionString(connectionString)
                .build();

        return MongoClients.create(mongoClientSettings);
    }

    @Override
    public Collection getMappingBasePackages() {
        return Collections.singleton("com.example.authenticationserver");
    }
}