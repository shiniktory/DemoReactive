package com.implemica.demo.data.repository;

import com.implemica.demo.data.document.Artist;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Mono;

public interface ArtistRepository extends ReactiveMongoRepository<Artist, String> {

   Mono<Artist> findByArtistName(String artistName);
}
