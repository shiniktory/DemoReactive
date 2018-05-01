package com.implemica.demo.data.document;

import lombok.*;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "artists")
@Getter
@Setter
@ToString(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class Artist extends AbstractEntity {

   @Indexed
   private String artistName;

   private String imagePath;
}
