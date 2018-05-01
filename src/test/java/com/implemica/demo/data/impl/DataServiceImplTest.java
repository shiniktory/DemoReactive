package com.implemica.demo.data.impl;

import com.implemica.demo.data.DataService;
import com.implemica.demo.data.document.Artist;
import com.implemica.demo.data.document.News;
import com.implemica.demo.data.document.NewsType;
import com.implemica.demo.data.dto.NewsForm;
import com.implemica.demo.data.repository.ArtistRepository;
import com.implemica.demo.data.repository.NewsRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import reactor.test.StepVerifier;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest
public class DataServiceImplTest {

   @Autowired
   private NewsRepository newsRepository;

   @Autowired
   private ArtistRepository artistRepository;

   @Autowired
   private DataService dataService;

   @Before
   public void init() {
      dataService.insertTestData();

      List<Artist> artists = artistRepository.findAll().collectList().block();
      assertThat(artists).isNotEmpty();

      List<News> news = newsRepository.findAll().collectList().block();
      assertThat(news).isNotEmpty();
   }

   @Test
   public void testListArtists() {
      List<Artist> testArtists = artistRepository.findAll().collectList().block();

      assertThat(testArtists).isNotEmpty();

      // FUNCTIONALITY/TESTS
      StepVerifier.create(dataService.listArtists())
              .expectNextSequence(testArtists)
              .verifyComplete();
   }

   @Test
   public void testFindArtist() {
      // PREDICATE
      Artist testArtist = artistRepository.findAll().collectList().block().iterator().next();
      String testId = testArtist.getId();

      // FUNCTIONALITY/TESTS
      StepVerifier.create(dataService.findArtist(testId))
              .expectNext(testArtist)
              .verifyComplete();
   }

   @Test
   public void testCreateNewPost() {
      // PREDICATE
      Artist testArtist = artistRepository.findAll().collectList().block().iterator().next();

      NewsForm newsToSave = NewsForm.builder()
              .newsType(NewsType.CONCERT)
              .newsText(UUID.randomUUID().toString())
              .artistId(testArtist.getId())
              .build();

      // FUNCTIONALITY
      News savedNews = dataService.createNewPost(newsToSave)
              .block();

      // TESTS
      assertThat(savedNews).isNotNull();
      assertThat(savedNews.getArtist()).isEqualTo(testArtist);
      assertThat(savedNews.getText()).isEqualTo(newsToSave.getNewsText());
      assertThat(savedNews.getType()).isEqualTo(newsToSave.getNewsType());

      News actualSavedNews = newsRepository.findById(savedNews.getId()).block();
      assertThat(actualSavedNews).isNotNull();
      assertThat(savedNews).isEqualTo(actualSavedNews);
   }

   @Test
   public void testGetTopArtistStatistics() {
      // PREDICATE
      Artist testArtist = artistRepository.findAll().collectList().block().iterator().next();

      newsRepository.save(News.builder()
              .artist(testArtist)
              .publishedTime(Instant.now())
              .text(UUID.randomUUID().toString())
              .type(NewsType.CONCERT)
              .build()).block();

      // FUNCTIONALITY/TESTS
      StepVerifier.create(dataService.getTopArtistStatistics(2L))
              .assertNext(artistNewsStatistic -> {
                 assertThat(artistNewsStatistic.getNewsCount()).isEqualTo(2L);
                 assertThat(artistNewsStatistic.getArtistName()).isEqualTo(testArtist.getArtistName());
              })
              .expectNextCount(1)
              .verifyComplete();
   }
}