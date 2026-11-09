package entity;
public class Order {
 private final int id,price,customerID,quantity; private final String dateTime,itemName;
 public Order(int id,int price,String dateTime,String itemName,int customerID){this(id,price,dateTime,itemName,customerID,1);}
 public Order(int id,int price,String dateTime,String itemName,int customerID,int quantity){this.id=id;this.price=price;this.dateTime=dateTime;this.itemName=itemName;this.customerID=customerID;this.quantity=quantity;}
 public int getID(){return id;} public int getPrice(){return price;} public String getDateTime(){return dateTime;} public String getItemName(){return itemName;} public int getCustomerID(){return customerID;} public int getQuantity(){return quantity;}
}
