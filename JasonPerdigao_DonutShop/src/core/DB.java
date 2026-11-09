package core;
import java.sql.*;
/** Embedded Derby database for Oak Donuts. */
public final class DB {
 private static DB instance; private final Connection connection;
 private DB() throws SQLException { connection=DriverManager.getConnection("jdbc:derby:oakdonutsdb;create=true"); initializeSchema(); }
 public static synchronized DB getInstance(){try{if(instance==null||instance.connection.isClosed())instance=new DB();return instance;}catch(SQLException e){throw new IllegalStateException("Could not connect to Oak Donuts database: "+e.getMessage(),e);}}
 private boolean exists(String name)throws SQLException{try(ResultSet r=connection.getMetaData().getTables(null,null,name,new String[]{"TABLE"})){return r.next();}}
 private void initializeSchema()throws SQLException{try(Statement s=connection.createStatement()){
  if(!exists("DONUT_CUSTOMER"))s.executeUpdate("CREATE TABLE DONUT_CUSTOMER (CUSTOMER_ID INT NOT NULL PRIMARY KEY,FIRST_NAME VARCHAR(40) NOT NULL,LAST_NAME VARCHAR(40) NOT NULL,FAVORITE_DONUT VARCHAR(60) NOT NULL)");
  if(!exists("DONUT_ORDER"))s.executeUpdate("CREATE TABLE DONUT_ORDER (ORDER_ID INT NOT NULL PRIMARY KEY,TOTAL_CENTS INT NOT NULL,ORDER_DATE TIMESTAMP NOT NULL,DONUT_NAME VARCHAR(80) NOT NULL,CUSTOMER_ID INT NOT NULL,QUANTITY INT NOT NULL,FOREIGN KEY (CUSTOMER_ID) REFERENCES DONUT_CUSTOMER(CUSTOMER_ID))");
 }}
 public PreparedStatement prepare(String sql)throws SQLException{return connection.prepareStatement(sql);}
 public Connection connection(){return connection;}
}
