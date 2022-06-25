package labs;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;

@Entity
public class User {

   @Id
   @GeneratedValue(strategy=GenerationType.AUTO)
   private Integer id;
   private String firstName;
   private String lastName;

   @Column(unique=true)
   private String email;
   private String password;
   private Boolean isAdmin;

   public User() {

   }

   public User(Integer id, String firstName, String lastName, String email, String password, Boolean isAdmin) {
      super();
      this.id = id;
      this.firstName = firstName;
      this.lastName = lastName;
      this.email = email;
      this.password = password;
      this.isAdmin = isAdmin;
   }

   public Integer getId() {
      return id;
   }
   public void setId(Integer id) {
      this.id = id;
   }

   public String getFirstName() {
      return firstName;
   }
   public void setFirstName(String firstName) {
      this.firstName = firstName;
   }

   public String getLastName() {
      return lastName;
   }
   public void setLastName(String lastName) {
      this.lastName = lastName;
   }

   public String getEmail() {
      return email;
   }
   public void setEmail(String email) {
      this.email = email;
   }

   public String getPassword() {
      return password;
   }
   public void setPassword(String password) {
      this.password = password;
   }

   public Boolean getIsAdmin() {
      return isAdmin;
   }
   public void setIsAdmin(Boolean isAdmin) {
      this.isAdmin = isAdmin;
   }

}
