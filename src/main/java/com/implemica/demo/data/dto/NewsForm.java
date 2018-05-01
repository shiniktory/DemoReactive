package com.implemica.demo.data.dto;

import com.implemica.demo.data.document.NewsType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * Created by User on 29.04.2018.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NewsForm {

   @NotNull
   private String artistId;

   @NotNull
   private NewsType newsType;

   @NotBlank
   private String newsText;
}
