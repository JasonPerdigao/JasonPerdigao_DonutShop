package entity;
public class Customer {
 private final int id; private final String firstName,lastName,favoriteDonut;
 public Customer(int id,String firstName,String lastName,String favoriteDonut){this.id=id;this.firstName=firstName;this.lastName=lastName;this.favoriteDonut=favoriteDonut;}
 public int getID(){return id;} public String getFirstName(){return firstName;} public String getLastName(){return lastName;}
 public String getFavoriteMeal(){return favoriteDonut;} public String getFavoriteDonut(){return favoriteDonut;}
}
