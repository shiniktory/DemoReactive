package com.implemica.demo;

import com.implemica.demo.data.DataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;


@SpringBootApplication
@Import(MongoConfig.class)
public class DemoApplication {

   public static void main(String[] args) {
      SpringApplication.run(DemoApplication.class);
   }


   @Autowired
   private DataService dataService;

   @Bean
   CommandLineRunner initData() {

      return (p) -> dataService.insertTestData();
   }

}
