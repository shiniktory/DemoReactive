package com.implemica.demo.controller;

import com.implemica.demo.DemoApplication;
import com.implemica.demo.data.DataService;
import com.implemica.demo.data.document.Artist;
import com.implemica.demo.data.document.News;
import com.implemica.demo.data.document.NewsType;
import com.implemica.demo.data.dto.NewsForm;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

import java.util.UUID;


@RunWith(SpringRunner.class)
@SpringBootTest(classes = DemoApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class RequestHandlerTest {

   @Autowired
   private DataService dataService;

   @LocalServerPort
   private int port;

   @Before
   public void insertTestDate() {
      dataService.insertTestData();
   }

   @Test
   public void testRoutesForFunction() {
      // PREDICATE
      RouterFunction<ServerResponse> route = RouterFunctions.route(
              RequestPredicates.GET("/"),
              request -> ServerResponse.ok()
                      .build());

      WebTestClient client = WebTestClient
              .bindToRouterFunction(route)
              .build();

      // FUNCTIONALITY/TESTS
      client.get()
              .uri("/")
              .exchange()
              .expectStatus()
              .isOk();

      // PREDICATE
      route = RouterFunctions.route(
              RequestPredicates.GET("/update_top"),
              request -> ServerResponse.ok()
                      .build());

      client = WebTestClient
              .bindToRouterFunction(route)
              .build();

      // FUNCTIONALITY/TESTS
      client.get()
              .uri("/update_top")
              .exchange()
              .expectStatus()
              .isOk();
   }

   @Test
   public void testRoutesForServer() {
      // PREDICATE

      Artist testArtist = dataService.listArtists()
              .collectList()
              .block()
              .iterator()
              .next();

      WebTestClient client = WebTestClient
              .bindToServer()
              .baseUrl("http://localhost:" + port)
              .build();

      NewsForm newsToSave = NewsForm.builder()
              .newsType(NewsType.CONCERT)
              .newsText(UUID.randomUUID().toString())
              .artistId(testArtist.getId())
              .build();


      // FUNCTIONALITY/TESTS
      client.post()
              .uri("/add_news")
              .contentType(MediaType.APPLICATION_FORM_URLENCODED)
              .body(BodyInserters
                      .fromFormData("artistId", newsToSave.getArtistId())
                      .with("newsText", newsToSave.getNewsText())
                      .with("newsType", newsToSave.getNewsType().name()))
              .exchange()
              .expectStatus()
              .isOk()
              .expectBody(News.class);
   }
}