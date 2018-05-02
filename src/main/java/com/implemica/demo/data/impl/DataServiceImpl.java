package com.implemica.demo.data.impl;

import com.implemica.demo.data.DataService;
import com.implemica.demo.data.document.Artist;
import com.implemica.demo.data.document.News;
import com.implemica.demo.data.document.NewsType;
import com.implemica.demo.data.repository.ArtistRepository;
import com.implemica.demo.data.repository.NewsRepository;
import com.implemica.demo.data.dto.ArtistNewsStatistic;
import com.implemica.demo.data.dto.NewsForm;
import org.apache.commons.lang3.StringUtils;
import org.reactivestreams.Publisher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.CollectionOptions;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;


@Component
public class DataServiceImpl implements DataService {

   @Autowired
   private NewsRepository newsRepository;

   @Autowired
   private ArtistRepository artistRepository;

   @Autowired
   private ReactiveMongoTemplate reactiveMongoTemplate;

   @Override
   public Flux<Artist> listArtists() {

      Flux<Artist> allArtists = artistRepository.findAll()
              .log("All Artists");

      allArtists.subscribe();

      return allArtists;
   }

   @Override
   public Mono<Artist> findArtist(String artistId) {

      if (StringUtils.isBlank(artistId)) {
         return Mono.empty();
      }

      Mono<Artist> foundArtist = artistRepository.findById(artistId)
              .log("Selected artist");

      foundArtist.subscribe();

      return foundArtist;
   }

   @Override
   public Flux<News> allNewsTailable() {

      return newsRepository.findBy()
              .log("allNews");
   }

   @Override
   public Mono<News> createNewPost(NewsForm newForm) {

      if (newForm == null) {
         return Mono.empty();
      }

      Mono<Artist> artistById = artistRepository.findById(newForm.getArtistId())
              .log("Artist from form");

      artistById.subscribe();

      return artistById
              .flatMap(artist -> Mono.just(News.builder()
                      .artist(artist)
                      .text(newForm.getNewsText())
                      .publishedTime(Instant.now())
                      .type(newForm.getNewsType())
                      .build())
              ).log("News converted")
              .flatMap(news -> newsRepository.save(news))
              .log("News saved");
   }

   @Override
   public Flux<ArtistNewsStatistic> getTopArtistStatistics(Long topCount) {

      Flux<ArtistNewsStatistic> topArtistsStat = artistRepository.findAll()
              .flatMap(this::mapToTopArtistItem)
              .filter(artistNewsStatistic -> artistNewsStatistic.getNewsCount() > 0)
              .log("Artists filtered")
              .sort(Comparator.comparing(ArtistNewsStatistic::getNewsCount).reversed())
              .log("Artists sorted")
              .take(topCount);

      topArtistsStat.subscribe();

      return topArtistsStat;
   }


   private Publisher<ArtistNewsStatistic> mapToTopArtistItem(Artist artist) {

      Mono<ArtistNewsStatistic> artistNewsStat = newsRepository.countAllByArtist(artist)
              .map(count -> mapToArtistItemImpl(artist, count))
              .log("Converted to Item");

      artistNewsStat.subscribe();

      return artistNewsStat;
   }

   private ArtistNewsStatistic mapToArtistItemImpl(Artist artist, Long count) {

      return ArtistNewsStatistic.builder()
              .newsCount(count)
              .artistName(artist.getArtistName())
              .build();
   }


   // TEST DATA


   @Override
   public void insertTestData() {
      insertArtists();

      insertNews();
   }

   private void insertArtists() {
      reactiveMongoTemplate.dropCollection(Artist.class).block();

      reactiveMongoTemplate.createCollection(
              Artist.class,
              CollectionOptions.empty()
                      .size(104857600)
                      .capped())
              .block();

      artistRepository.save(new Artist("Jinjer", "/images/Jinjer.jpg"))
              .and(artistRepository.save(
                      new Artist("Metallica", "/images/Metallica.jpg"))
                      .log("init"))
              .and(artistRepository.save(
                      new Artist("The Hardkiss", "/images/TheHardkiss.jpg"))
                      .log("init"))
              .and(artistRepository.save(
                      new Artist("-deTach-", "/images/Detach.jpg"))
                      .log("init"))
              .and(artistRepository.save(
                      new Artist("Lindsey Stirling", "/images/LindseyStirling.jpg"))
                      .log("init"))
              .block(Duration.ofSeconds(25));
   }

   private void insertNews() {
      reactiveMongoTemplate.dropCollection(News.class).block();

      reactiveMongoTemplate.createCollection(
              News.class,
              CollectionOptions.empty()
                      .size(104857600)
                      .capped())
              .block();

      newsRepository.save(News.builder()
              .artist(artistRepository.findByArtistName("Jinjer")
                      .block())
              .publishedTime(Instant.now())
              .text("Concert in Kharkov!!!")
              .type(NewsType.CONCERT)
              .build()
      ).and(newsRepository.save(
              News.builder()
                      .artist(artistRepository.findByArtistName("Lindsey Stirling")
                              .log("Artist for News")
                              .block())
                      .publishedTime(Instant.now().minus(6, ChronoUnit.DAYS))
                      .text("New album: Warmer in the Winter")
                      .type(NewsType.RELEASE)
                      .build())
              .log("init"))
              .and(newsRepository.save(
                      News.builder()
                              .artist(artistRepository.findByArtistName("Metallica")
                                      .log("Artist for News")
                                      .block())
                              .publishedTime(Instant.now().minus(31, ChronoUnit.DAYS))
                              .text("On tour")
                              .type(NewsType.CONCERT)
                              .build())
                      .log("init"))
              .block(Duration.ofSeconds(25));
   }
}
