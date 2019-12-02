package com.b07.database.helper;

import android.content.Context;

import com.b07.exceptions.DatabaseInsertException;
import com.b07.exceptions.DuplicateUserLoginException;
import com.b07.exceptions.InvalidItemException;
import com.b07.exceptions.InvalidQuantityException;
import com.b07.exceptions.InvalidPriceException;
import com.b07.exceptions.InvalidRoleException;
import com.b07.exceptions.InvalidUserLoginException;
import com.b07.exceptions.ItemNotFoundException;
import com.b07.exceptions.NullParameterException;
import com.b07.exceptions.SaleNotFoundException;
import com.b07.exceptions.UserNotFoundException;

import java.math.BigDecimal;
import java.sql.SQLException;


public class DatabaseInsertHelper {

  /**
   * Inserts new roles into the database.
   * @return the id of the role that was inserted.
   * @throws DatabaseInsertException if there is a failure on the insert.
   * @throws InvalidRoleException if name is not a valid role.
   * @throws NullParameterException if name is null.
   * @throws SQLException if something goes wrong.
   */
  public static int insertRole(String name, Context context) throws DatabaseInsertException {
      DatabaseDriverAndroid db = new DatabaseDriverAndroid(context);
      int id = -1;
      try {
          id = (int) db.insertRole(name);
      } catch (Exception e) {
          throw new DatabaseInsertException();
      }
      return id;
  }
  
  /**
   * Use this to insert a new user.
   * @param name the user's name.
   * @param age the user's age, bound to the interval [0, 122].
   * @param address the user's address, truncated to 100 characters.
   * @param password the user's password (not hashed).
   * @return the user id
   * @throws DatabaseInsertException if there is a failure on the insert.
   * @throws NullParameterException if name, address, or password is null.
   * @throws SQLException if something goes wrong.
   */
  public static int insertNewUser(String name, int age, String address, String password,
                                  String login, Context context)
      throws DatabaseInsertException, NullParameterException, InvalidUserLoginException, DuplicateUserLoginException {
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
      DatabaseDriverAndroid db = new DatabaseDriverAndroid(context);

    if (login.length() > 64) {
      throw new InvalidUserLoginException();
    }
    //try {
    //  db.getUserIdByLogin(login);
    //    throw new DuplicateUserLoginException();
    //} catch (UserNotFoundException e) {
    //}
    int userId = -1;
    try{
        userId = (int)db.insertNewUser(name, age, address, password);
    }catch (Exception e){
        throw new DatabaseInsertException();
    }

    return userId;
  }
  
  /**
   * Insert a relationship between a user and a role.
   * @param userId the id of the user.
   * @param roleId the role id of the user.

   * @return the unique relationship id.
   * @throws DatabaseInsertException if there is a failure on the insert.
   * @throws SQLException if something goes wrong.
   * @throws UserNotFoundException if userId is not found in the database.
   */
  public static int insertUserRole(int userId, int roleId,Context context)
          throws DatabaseInsertException {
    DatabaseDriverAndroid db = new DatabaseDriverAndroid(context);
    int userRoleId = -1;
    try {
      userRoleId = (int) db.insertUserRole(userId, roleId);
    } catch (Exception e) {
      throw new DatabaseInsertException();
    }

    return userRoleId;
  }

  /**
   * insert an item into the database.
   * @param name the name of the item, truncated to 64 characters.
   * @param price the price of the item.
   * @return the id of the inserted record.
   * @throws DatabaseInsertException if there is a failure on the insert.
   * @throws NullParameterException if name or price is null.
   * @throws InvalidItemException if name is not a valid item type.
   * @throws InvalidPriceException if price < 0.005.
   */
  public static int insertItem(String name, BigDecimal price, Context context)
          throws NullParameterException, InvalidPriceException, DatabaseInsertException {
    if (name == null || price == null || context == null) {
      throw new NullParameterException();
    }
    price.setScale(2, BigDecimal.ROUND_HALF_UP);
    if (price.compareTo(new BigDecimal(0)) <= 0) {
      throw new InvalidPriceException();
    }
    if (name.length() > 64) {
      name = name.substring(0, 64);
    }
    DatabaseDriverAndroid db = new DatabaseDriverAndroid(context);
    try{
        return (int) db.insertItem(name, price);
    }catch (Exception e){
        throw new DatabaseInsertException();
      }
  }

  /**
   * insert inventory into the database.
   * @param itemId the id of the item.
   * @param quantity the quantity of the item.
   * @return the id of the inserted record.
   * @throws InvalidQuantityException if quantity < 0.
   * @throws DatabaseInsertException if there is a failure on the insert.
   * @throws ItemNotFoundException if itemId is not in the database.
   * @throws SQLException if something goes wrong.
   */
  public static int insertInventory(int itemId, int quantity, Context context)
          throws InvalidQuantityException, ItemNotFoundException, SQLException {
    try {
      DatabaseSelectHelper.getItem(itemId, context);
    } catch (Exception e) {
      throw new ItemNotFoundException();
    }
    if (quantity < 0) {
      throw new InvalidQuantityException();
    }
    
    int inventoryId = -1;
    DatabaseDriverAndroid db = new DatabaseDriverAndroid(context);
    try{
        inventoryId = (int) db.insertInventory(itemId, quantity);
    }catch (Exception e){
        throw new SQLException();
    }
    return inventoryId;
  }
  
  /**
   * insert a sale into the database.
   * @param userId the id of the user.
   * @param totalPrice the total price of the sale.
   * @return the id of the inserted record.
   * @throws DatabaseInsertException if there is a failure on the insert.
   * @throws InvalidPriceException if totalPrice < 0.005
   * @throws NullParameterException if totalPrice is null.
   * @throws UserNotFoundException if userId is not in the database.
   * @throws SQLException if something goes wrong.
   */
  public static int insertSale(int userId, BigDecimal totalPrice, Context context)
          throws DatabaseInsertException, UserNotFoundException, InvalidPriceException, NullParameterException {
      DatabaseDriverAndroid db = new DatabaseDriverAndroid(context);
      try {
      // Need to fix this method
      DatabaseSelectHelper.getUserDetails(userId, context);
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
    
    int saleId = -1;

    try{
        saleId = (int)db.insertSale(userId, totalPrice);
    }catch (Exception e){
        throw new DatabaseInsertException();
    }
    return saleId;
  }

  /**
   * insert an itemized record for a specific item in a sale.
   * @param saleId the id of the sale.
   * @param itemId the id of the item.
   * @param quantity the number of the item purchased.
   * @return the id of the inserted record.
   * @throws DatabaseInsertException if there is a failure on the insert.
   * @throws SaleNotFoundException if saleId is not in the database.
   * @throws ItemNotFoundException if itemId is not in the database.
   * @throws InvalidQuantityException if quantity < 0.
   * @throws SQLException if something goes wrong.
   */
  public static int insertItemizedSale(int saleId, int itemId, int quantity, Context context)
          throws DatabaseInsertException, ItemNotFoundException, InvalidQuantityException,
          SaleNotFoundException {
    if (quantity < 0) {
      throw new InvalidQuantityException();
    }
    try {
      DatabaseSelectHelper.getSaleById(saleId, context);
    } catch (Exception e) {
      throw new SaleNotFoundException();
    }
    try {
      DatabaseSelectHelper.getItem(itemId, context);
    } catch (Exception e) {
      throw new ItemNotFoundException();
    }
    
    int itemizedId = -1;
    DatabaseDriverAndroid db = new DatabaseDriverAndroid(context);
    try {
        itemizedId = (int) db.insertItemizedSale(saleId, itemId, quantity);
    }catch(Exception e){
        throw new DatabaseInsertException();
    }
    return itemizedId;
  }
  
  public static int insertAccount(int userId, boolean active, Context context)
          throws DatabaseInsertException {
      int accountId = -1;
      DatabaseDriverAndroid db = new DatabaseDriverAndroid(context);
      try {
          accountId = (int) db.insertAccount(userId, active);
      } catch (Exception e) {
          throw new DatabaseInsertException();
      }
      return accountId;
  }
	  
  public static int insertAccountLine(int accountId, int itemId, int quantity, Context context)
          throws DatabaseInsertException {
      int id = -1;
      DatabaseDriverAndroid db = new DatabaseDriverAndroid(context);
      try{
          id = (int) db.insertAccountLine(accountId, itemId, quantity);
      }catch (Exception e){
          throw new DatabaseInsertException();
      }
      return id;
	}
}
