package entity;
import core.DB;import java.sql.*;import java.util.*;
public class CustomerDAO implements DAO<Customer>{
 private Customer map(ResultSet r)throws SQLException{return new Customer(r.getInt("CUSTOMER_ID"),r.getString("FIRST_NAME"),r.getString("LAST_NAME"),r.getString("FAVORITE_DONUT"));}
 public Optional<Customer> get(int id){try(PreparedStatement s=DB.getInstance().prepare("SELECT * FROM DONUT_CUSTOMER WHERE CUSTOMER_ID=?")){s.setInt(1,id);try(ResultSet r=s.executeQuery()){return r.next()?Optional.of(map(r)):Optional.empty();}}catch(SQLException e){throw new IllegalStateException(e.getMessage(),e);}}
 public List<Customer> getAll(){List<Customer> a=new ArrayList<>();try(Statement s=DB.getInstance().connection().createStatement();ResultSet r=s.executeQuery("SELECT * FROM DONUT_CUSTOMER ORDER BY CUSTOMER_ID")){while(r.next())a.add(map(r));return a;}catch(SQLException e){throw new IllegalStateException(e.getMessage(),e);}}
 public void insert(Customer c){try(PreparedStatement s=DB.getInstance().prepare("INSERT INTO DONUT_CUSTOMER VALUES (?,?,?,?)")){s.setInt(1,c.getID());s.setString(2,c.getFirstName());s.setString(3,c.getLastName());s.setString(4,c.getFavoriteDonut());s.executeUpdate();}catch(SQLException e){throw new IllegalStateException(e.getMessage(),e);}}
 public void update(Customer c){try(PreparedStatement s=DB.getInstance().prepare("UPDATE DONUT_CUSTOMER SET FIRST_NAME=?,LAST_NAME=?,FAVORITE_DONUT=? WHERE CUSTOMER_ID=?")){s.setString(1,c.getFirstName());s.setString(2,c.getLastName());s.setString(3,c.getFavoriteDonut());s.setInt(4,c.getID());s.executeUpdate();}catch(SQLException e){throw new IllegalStateException(e.getMessage(),e);}}
 public void delete(Customer c){try(PreparedStatement s=DB.getInstance().prepare("DELETE FROM DONUT_CUSTOMER WHERE CUSTOMER_ID=?")){s.setInt(1,c.getID());s.executeUpdate();}catch(SQLException e){throw new IllegalStateException(e.getMessage(),e);}}
 public List<String> getColumnNames(){return List.of("CUSTOMER_ID","FIRST_NAME","LAST_NAME","FAVORITE_DONUT");}
}
