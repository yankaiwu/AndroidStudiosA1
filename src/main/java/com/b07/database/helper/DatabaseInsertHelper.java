package com.b07.database.helper;

import com.b07.exceptions.DatabaseInsertException;
import com.b07.exceptions.InvalidItemException;
import com.b07.exceptions.InvalidQuantityException;
import com.b07.exceptions.InvalidPriceException;
import com.b07.exceptions.InvalidRoleException;
import com.b07.exceptions.ItemNotFoundException;
import com.b07.exceptions.NullParameterException;
import com.b07.exceptions.SaleNotFoundException;
import com.b07.exceptions.UserNotFoundException;
import com.b07.inventory.ItemImpl;
import com.b07.inventory.ItemTypes;
import com.b07.store.Sale;
import com.b07.users.Roles;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;


public class DatabaseInsertHelper {

  /**
   * Inserts new roles into the database.
   * @param role the new role to be added.
   * @param connection the database.
   * @return the id of the role that was inserted.
   * @throws DatabaseInsertException if there is a failure on the insert.
   * @throws InvalidRoleException if name is not a valid role.
   * @throws NullParameterException if name is null.
   * @throws SQLException if something goes wrong.
   */
  public static int insertRole(String name) throws DatabaseInsertException, InvalidRoleException, SQLException,
      NullParameterException {
    if (name == null) {
      throw new NullParameterException();
    }
     int roleId;
    try {
      Roles.valueOf(name.toUpperCase());
      roleId = DatabaseSelectHelper.getRoleIdByName(name);
    } catch (IllegalArgumentException e) {
      throw new InvalidRoleException();
    } catch (SQLException e) {
      Connection connection = DatabaseDriverHelper.connectOrCreateDataBase();
      roleId = DatabaseDriverAndroid.insertRole(name.toUpperCase(), connection);
      connection.close();
    }
    
    return roleId;
  }
  
  /**
   * Use this to insert a new user.
   * @param name the user's name.
   * @param age the user's age, bound to the interval [0, 122].
   * @param address the user's address, truncated to 100 characters.
   * @param password the user's password (not hashed).
   * @param connection the database connection.
   * @return the user id
   * @throws DatabaseInsertException if there is a failure on the insert.
   * @throws NullParameterException if name, address, or password is null.
   * @throws SQLException if something goes wrong.
   */
  public static int insertNewUser(String name, int age, String address, String password) throws
      DatabaseInsertException, NullParameterException, SQLException {
    if (name == null || address == null || password == null) {
      throw new NullParameterException();
    }
    if (address.length() > 100) {
      address = address.substring(0, 100);
    }
    if (age < 0) {
      age = 0;
    }
    else if (age > 122) {
      age = 122;
    }
    
    Connection connection = DatabaseDriverHelper.connectOrCreateDataBase();
    int userId = DatabaseInserter.insertNewUser(name, age, address, password, connection);
    connection.close();
    return userId;
  }
  
  /**
   * Insert a relationship between a user and a role.
   * @param userId the id of the user.
   * @param roleId the role id of the user.
   * @param connection the database connection.
   * @return the unique relationship id.
   * @throws DatabaseInsertException if there is a failure on the insert.
   * @throws SQLException if something goes wrong.
   * @throws UserNotFoundException if userId is not found in the database.
   */
  public static int insertUserRole(int userId, int roleId) throws DatabaseInsertException, SQLException,
      UserNotFoundException {
    
    try {
      DatabaseSelectHelper.getUserDetails(userId);
    } catch (SQLException e) {
      throw new UserNotFoundException();
    }
    
    int userRoleId;
    try {
      userRoleId = DatabaseSelectHelper.getUserRoleId(userId);
    } catch (SQLException e) {
      Connection connection = DatabaseDriverHelper.connectOrCreateDataBase();
      userRoleId = DatabaseInserter.insertUserRole(userId, roleId, connection);
      if (DatabaseSelectHelper.getRoleName(roleId).equals("CUSTOMER")) {
        insertAccount(userId, true);
      }
      connection.close();
    }
    return userRoleId;
  }

  /**
   * insert an item into the database.
   * @param name the name of the item, truncated to 64 characters.
   * @param price the price of the item.
   * @param connection the database connection.
   * @return the id of the inserted record.
   * @throws DatabaseInsertException if there is a failure on the insert.
   * @throws NullParameterException if name or price is null.
   * @throws InvalidItemException if name is not a valid item type.
   * @throws InvalidPriceException if price < 0.005
   * @throws SQLException if something goes wrong.
   */
  public static int insertItem(String name, BigDecimal price) throws DatabaseInsertException,
      NullParameterException, InvalidItemException, InvalidPriceException, SQLException {
    if (name == null || price == null) {
      throw new NullParameterException();
    }
    price.setScale(2, BigDecimal.ROUND_HALF_UP);
    if (price.compareTo(new BigDecimal(0)) <= 0) {
      throw new InvalidPriceException();
    }
    if (name.length() > 64) {
      name = name.substring(0, 64);
    }
    try {
      ItemTypes.valueOf(name.toUpperCase());
    } catch (Exception e) {
      throw new InvalidItemException();
    }
    
    Connection connection = DatabaseDriverHelper.connectOrCreateDataBase();
    int itemId = DatabaseInserter.insertItem(name, price, connection);
    connection.close();
    return itemId;
  }

  /**
   * insert inventory into the database.
   * @param itemId the id of the item.
   * @param quantity the quantity of the item.
   * @param connection the database connection.
   * @return the id of the inserted record.
   * @throws InvalidQuantityException if quantity < 0.
   * @throws DatabaseInsertException if there is a failure on the insert.
   * @throws ItemNotFoundException if itemId is not in the database.
   * @throws SQLException if something goes wrong.
   */
  public static int insertInventory(int itemId, int quantity) throws DatabaseInsertException,
      InvalidQuantityException, ItemNotFoundException, SQLException {
    try {
      DatabaseSelectHelper.getItem(itemId);
    } catch (Exception e) {
      throw new ItemNotFoundException();
    }
    if (quantity < 0) {
      throw new InvalidQuantityException();
    }
    
    Connection connection = DatabaseDriverHelper.connectOrCreateDataBase();
    int inventoryId = DatabaseInserter.insertInventory(itemId, quantity, connection);
    connection.close();
    return inventoryId;
  }
  
  /**
   * insert a sale into the database.
   * @param userId the id of the user.
   * @param totalPrice the total price of the sale.
   * @param connection the database connection.
   * @return the id of the inserted record.
   * @throws DatabaseInsertException if there is a failure on the insert.
   * @throws InvalidPriceException if totalPrice < 0.005
   * @throws NullParameterException if totalPrice is null.
   * @throws UserNotFoundException if userId is not in the database.
   * @throws SQLException if something goes wrong.
   */
  public static int insertSale(int userId, BigDecimal totalPrice) throws DatabaseInsertException,
      UserNotFoundException, InvalidPriceException, NullParameterException, SQLException {
    try {
      // Need to fix this method
      DatabaseSelectHelper.getUserDetails(userId);
    } catch (Exception e) {
      throw new UserNotFoundException();
    }
    if (totalPrice == null) {
      throw new NullParameterException();
    }
    totalPrice = totalPrice.setScale(2, BigDecimal.ROUND_HALF_UP);
    if (totalPrice.compareTo(new BigDecimal(0)) <= 0) {
      throw new InvalidPriceException();
    }
    
    Connection connection = DatabaseDriverHelper.connectOrCreateDataBase();
    int saleId = DatabaseInserter.insertSale(userId, totalPrice, connection);
    connection.close();
    return saleId;
  }

  /**
   * insert an itemized record for a specific item in a sale.
   * @param saleId the id of the sale.
   * @param itemId the id of the item.
   * @param quantity the number of the item purchased.
   * @param connection the database connection.
   * @return the id of the inserted record.
   * @throws DatabaseInsertException if there is a failure on the insert.
   * @throws SaleNotFoundException if saleId is not in the database.
   * @throws ItemNotFoundException if itemId is not in the database.
   * @throws InvalidQuantityException if quantity < 0.
   * @throws SQLException if something goes wrong.
   * @throws UserNotFoundException if saleId is associated with a user not in the database.
   */
  public static int insertItemizedSale(int saleId, int itemId, int quantity) throws DatabaseInsertException,
      ItemNotFoundException, InvalidQuantityException, SaleNotFoundException, SQLException, UserNotFoundException {
    if (quantity < 0) {
      throw new InvalidQuantityException();
    }
    try {
      DatabaseSelectHelper.getSaleById(saleId);
    } catch (Exception e) {
      throw new SaleNotFoundException();
    }
    try {
      DatabaseSelectHelper.getItem(itemId);
    } catch (Exception e) {
      throw new ItemNotFoundException();
    }
    
    Sale itemizedSale = DatabaseSelectHelper.getItemizedSaleById(saleId);
    if (itemizedSale.getItemMap().containsKey(new ItemImpl(itemId, null, null))) {
      throw new DatabaseInsertException();
    }
    
    Connection connection = DatabaseDriverHelper.connectOrCreateDataBase();
    int itemizedId = DatabaseInserter.insertItemizedSale(saleId, itemId, quantity, connection);
    connection.close();
    return itemizedId;
  }
  
  public static int insertAccount(int userId, boolean active) throws SQLException, DatabaseInsertException {
		Connection connection = DatabaseDriverHelper.connectOrCreateDataBase();
	  int accountId = DatabaseInserter.insertAccount(userId, active, connection);
	  connection.close();
		return accountId;
	  }
	  
  public static int insertAccountLine(int accountId, int itemId, int quantity) throws DatabaseInsertException,
      SQLException {
	  Connection connection = DatabaseDriverHelper.connectOrCreateDataBase();
	  int itemizedId = DatabaseInserter.insertAccountLine(accountId, itemId, quantity, connection);
	  connection.close();
		return itemizedId;
	}
}
