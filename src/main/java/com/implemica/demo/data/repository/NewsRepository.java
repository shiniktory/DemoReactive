package com.implemica.demo.data.repository;

import com.implemica.demo.data.document.Artist;
import com.implemica.demo.data.document.News;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.data.mongodb.repository.Tailable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


public interface NewsRepository extends ReactiveMongoRepository<News, String> {

   @Tailable
   Flux<News> findBy();

   Mono<Long> countAllByArtist(Artist artist);
}
