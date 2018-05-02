package com.implemica.demo;

import com.mongodb.reactivestreams.client.MongoClient;
import com.mongodb.reactivestreams.client.MongoClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.AbstractReactiveMongoConfiguration;
import org.springframework.data.mongodb.config.EnableMongoAuditing;
import org.springframework.data.mongodb.repository.config.EnableReactiveMongoRepositories;


@EnableReactiveMongoRepositories
@Configuration
@EnableMongoAuditing
public class MongoConfig extends AbstractReactiveMongoConfiguration {

   @Bean
   @Override
   public MongoClient reactiveMongoClient() {
      return MongoClients.create();
   }

   @Override
   protected String getDatabaseName() {
      return "reactive-demo";
   }
}
