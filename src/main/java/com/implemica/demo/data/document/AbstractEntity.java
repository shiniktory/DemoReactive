package com.implemica.demo.data.document;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.*;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Document
@Getter
@Setter
public class AbstractEntity {

   @Id
   private String id;

   @CreatedDate
   private Date createdTime;

   @LastModifiedDate
   private Date lastModifiedTime;

   @Override
   public int hashCode() {
      return (id == null) ? 0 : id.hashCode();
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj)
         return true;
      if (obj == null)
         return false;
      if (getClass() != obj.getClass())
         return false;
      AbstractEntity other = (AbstractEntity) obj;
      if (id == null)
         return other.id == null;
      return id.equals(other.id);
   }
}
