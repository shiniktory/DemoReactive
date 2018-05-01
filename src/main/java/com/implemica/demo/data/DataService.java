package com.implemica.demo.data;

import com.implemica.demo.data.document.Artist;
import com.implemica.demo.data.document.News;
import com.implemica.demo.data.dto.ArtistNewsStatistic;
import com.implemica.demo.data.dto.NewsForm;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


public interface DataService {

   Flux<Artist> listArtists();

   Mono<Artist> findArtist(String artistId);

   Flux<News> allNewsTailable();

   Mono<News> createNewPost(NewsForm newForm);

   Flux<ArtistNewsStatistic> getTopArtistStatistics(Long topCount);

   void insertTestData();

}
