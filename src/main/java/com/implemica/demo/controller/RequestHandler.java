package com.implemica.demo.controller;

import com.implemica.demo.data.DataService;
import com.implemica.demo.data.document.News;
import com.implemica.demo.data.document.NewsType;
import com.implemica.demo.data.dto.NewsForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.BodyExtractors;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;

import static org.springframework.web.reactive.function.server.RequestPredicates.*;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;
import static org.springframework.web.reactive.function.server.ServerResponse.ok;

@Component
public class RequestHandler {

   @Autowired
   private DataService dataService;

   @Bean
   public RouterFunction<ServerResponse> routes() {

      return route(GET("/").and(accept(MediaType.ALL)), this::handleStartPageRequest)

              .andRoute(GET("/find_artist/{artistId}").and(accept(MediaType.ALL)),
                      this::handleFindArtist)

              .andRoute(GET("/update_top").and(accept(MediaType.ALL)),
                      this::handleUpdateTopRequest)

              .andRoute(GET("/news").and(accept(MediaType.ALL)),
                      this::handleAllNewsRequest)

              .andRoute(POST("/add_news").and(accept(MediaType.APPLICATION_FORM_URLENCODED)),
                      this::handleAddNewsRequest);
   }

   /**
    * Equivalent to:
    * {@code @PostMapping("/add_news")}
    * public Mono<News> addMessage(NewsForm newMessage) {
    * <p>
    * return dataService.createNewPost(newMessage);
    * }
    */
   private Mono<ServerResponse> handleAddNewsRequest(ServerRequest request) {

      return request.body(BodyExtractors.toFormData())
              .map(stringStringMultiValueMap ->
                      NewsForm.builder()
                              .artistId(extractParameter(stringStringMultiValueMap, "artistId"))
                              .newsText(extractParameter(stringStringMultiValueMap, "newsText"))
                              .newsType(NewsType.valueOf(extractParameter(stringStringMultiValueMap, "newsType")))
                              .build()
              )
              .flatMap(newsForm -> ok().body(dataService.createNewPost(newsForm), News.class))
              .switchIfEmpty(ServerResponse.badRequest().build());
   }

   private String extractParameter(MultiValueMap<String, String> stringStringMultiValueMap, String artistId) {

      return stringStringMultiValueMap.get(artistId).iterator().next();
   }

   /**
    * Equivalent to:
    * <p>
    * {@code @GetMapping("/news")}
    * public Flux<News> allNews() {
    * <p>
    * return dataService.allNewsTailable();
    * }
    */
   private Mono<ServerResponse> handleAllNewsRequest(ServerRequest request) {

      return ok().contentType(MediaType.TEXT_EVENT_STREAM)
              .body(BodyInserters.fromPublisher(dataService.allNewsTailable(), News.class));
   }

   /**
    * Equivalent to:
    * {@code @GetMapping("/update_top")}
    * public String updateTop(Model model) {
    * <p>
    * model.addAttribute("topList",
    * dataService.getTopArtistStatistics(5L));
    * <p>
    * return "index :: #top_tbody";
    * }
    */
   private Mono<ServerResponse> handleUpdateTopRequest(ServerRequest request) {
      Map<String, Object> model = new HashMap<>();
      model.put("topList", dataService.getTopArtistStatistics(5L));

      return ok().contentType(MediaType.TEXT_HTML).render("index :: #top_tbody", model);
   }

   /**
    * Equivalent to:
    * {@code @GetMapping("/find_artist/{artistId}")}
    * public String addMessage(@PathVariable("artistId") String artistId, Model model) {
    * <p>
    * model.addAttribute("selectedArtist", dataService.findArtist(artistId));
    * <p>
    * return "index :: #selected_artist";
    * }
    */
   private Mono<ServerResponse> handleFindArtist(ServerRequest request) {
      Map<String, Object> model = new HashMap<>();
      model.put("selectedArtist", dataService.findArtist(request.pathVariable("artistId")));

      return ok().render("index :: #selected_artist", model);
   }

   /**
    * Equivalent to:
    * {@code @GetMapping("/")}
    * public String startPage(Model model) {
    * <p>
    * model.addAttribute("artists", dataService.listArtists());
    * model.addAttribute("newsTypes", Flux.just(NewsType.values()));
    * model.addAttribute("newsForm", new NewsForm());
    * model.addAttribute("topList", dataService.getTopArtistStatistics(5L));
    * <p>
    * return "index";
    * }
    */
   private Mono<ServerResponse> handleStartPageRequest(ServerRequest request) {
      Map<String, Object> model = new HashMap<>();

      model.put("topList", dataService.getTopArtistStatistics(5L));
      model.put("newsForm", Mono.just(new NewsForm()));
      model.put("newsTypes", Flux.just(NewsType.values()));
      model.put("artists", dataService.listArtists());

      return ok().render("index", model);
   }
}
