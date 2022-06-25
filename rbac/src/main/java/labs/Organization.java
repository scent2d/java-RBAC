package labs;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;

@Entity
public class Organization {

   @Id
   @GeneratedValue(strategy=GenerationType.AUTO)
   private Integer id;
   private String name;
   private String description;

   public Organization() {

   }

   public Organization(Integer id, String name, String description) {
      super();
      this.id = id;
      this.name = name;
      this.description = description;
   }

   public Integer getId() {
      return id;
   }
   public void setId(Integer id) {
      this.id = id;
   }

   public String getName() {
      return name;
   }
   public void setName(String name) {
      this.name = name;
   }

   public String getDescription() {
      return description;
   }
   public void setDescription(String description) {
      this.description = description;
   }

}
