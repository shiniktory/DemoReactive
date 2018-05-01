package com.implemica.demo.data.document;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.InstantSerializer;
import lombok.*;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;


@Getter
@Setter
@ToString(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "news")
@Builder
public class News extends AbstractEntity {

   @Indexed
   private Artist artist;

   private NewsType type;
   
   private String text;

   @JsonSerialize(contentUsing = InstantSerializer.class)
   private Instant publishedTime;
}
