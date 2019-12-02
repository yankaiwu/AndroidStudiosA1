package com.b07.database.helper;

import android.content.Context;

import androidx.core.database.DatabaseUtilsCompat;

import com.b07.exceptions.InvalidItemException;
import com.b07.exceptions.InvalidPriceException;
import com.b07.exceptions.InvalidQuantityException;
import com.b07.exceptions.InvalidRoleException;
import com.b07.exceptions.NullParameterException;
import com.b07.inventory.ItemTypes;
import com.b07.users.Roles;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;

public class DatabaseUpdateHelper{
  
  /**
   * Update the role name of a given role in the role table.
   * @param name the new name of the role.
   * @param id the id of the user.
   * @return true if the update is successful, otherwise false.
   * @throws NullParameterException if name is null.
   * @throws InvalidRoleException if name is not a valid role.
   * @throws SQLException if something goes wrong.
   */
  public static boolean updateRoleName(String name, int id, Context context) throws NullParameterException, InvalidRoleException,
      SQLException {
    // Need to check if id (role id) exists?
    DatabaseDriverAndroid db = new DatabaseDriverAndroid(context);
    if (name == null) {
      throw new NullParameterException();
    }
    boolean complete = false;
    try {
      Roles.valueOf(name.toUpperCase());
      complete = db.updateRoleName(name, id);
    } catch (Exception e) {
      throw new InvalidRoleException();
    }
    return complete;
  } 
  
  /**
   * Update the user name of the user associated with userId.
   * @param name the name of the user.
   * @param userId the id of the user.
   * @return true if the update is successful, otherwise false.
   * @throws NullParameterException if name is null.
   * @throws SQLException if something goes wrong.
   */
  public static boolean updateUserName(String name, int userId, Context context) throws NullParameterException, SQLException {
    // Need to check if userId exists?
    DatabaseDriverAndroid db = new DatabaseDriverAndroid(context);
    if (name == null) {
      throw new NullParameterException();
    }
    boolean complete = false;
    try {
      complete = db.updateUserName(name, userId);
    }catch (Exception e){
      throw new SQLException();
    }
    return complete;
  }
  
  /**
   * Update the user age of the user associated with userId.
   * @param age the age of the user, bound to the interval [0, 122].
   * @param userId the id of the user.
   * @return true if the update is successful, otherwise false.
   * @throws SQLException if something goes wrong.
   */
  public static boolean updateUserAge(int age, int userId, Context context) throws SQLException {
    // Need to check if userId exists?
    if (age < 0) {
      age = 0;
    }
    else if (age > 122) {
      age = 122;
    }
    boolean complete = false;
    DatabaseDriverAndroid db = new DatabaseDriverAndroid(context);
    try {
      complete = db.updateUserAge(age, userId);
    }catch (Exception e){
      throw new SQLException();
    }
    return complete;
  }
  
  /**
   * Update the address of the user associated with userId.
   * @param address the user's address, truncated to 100 characters.
   * @param userId the id of the user.
   * @return true if the update is successful, otherwise false.
   * @throws NullParameterException if address is null.
   * @throws SQLException if something goes wrong.
   */
  public static boolean updateUserAddress(String address, int userId, Context context) throws NullPointerException, SQLException {
    // Need to check if userId exists?
    if (address == null) {
      throw new NullPointerException();
    }
    if (address.length() > 100) {
      address = address.substring(0, 100);
    }
    boolean complete = false;
    DatabaseDriverAndroid db = new DatabaseDriverAndroid(context);
    try{
      complete = db.updateUserAddress(address, userId);
    }catch (Exception e){
      throw new SQLException();
    }
    return complete;

  }
  
  /**
   * Update the role id of the user associated with userId.
   * @param roleId the id of the role.
   * @param userId the id of the user.
   * @return true if the update is successful, otherwise false.
   * @throws SQLException if something goes wrong.
   */
  public static boolean updateUserRole(int roleId, int userId, Context context) throws SQLException {
    DatabaseDriverAndroid db = new DatabaseDriverAndroid(context);
    boolean complete = db.updateUserRole(roleId, userId);
    return complete;

  }
  
  /**
   * Update the name of the item associated with itemId.
   * @param name the item's name.
   * @param itemId the id of the item.
   * @return true if the update is successful, otherwise false.
   * @throws InvalidItemException if name is not a valid item.
   * @throws NullParameterException if name is null.
   * @throws SQLException if something goes wrong.
   */
  public static boolean updateItemName(String name, int itemId, Context context) throws InvalidItemException, NullParameterException,
      SQLException {
    if (name == null) {
      throw new NullParameterException();
    }
    if (name.length() > 64) {
      name = name.substring(0, 64);
    }
    try {
      ItemTypes.valueOf(name.toUpperCase());
    } catch (Exception e) {
      throw new InvalidItemException();
    }
    DatabaseDriverAndroid db = new DatabaseDriverAndroid(context);
    boolean complete = false;
    try {
      complete = db.updateItemName(name, itemId);
    }catch (Exception e){
      throw new SQLException();
    }
    return complete;

  }
  
  /**
   * Update the price of the item associated with itemId.
   * @param price the item's price.
   * @param itemId the id of the item.
   * @return true if the update is successful, otherwise false.
   * @throws NullParameterException if price is null.
   * @throws InvalidPriceException if price < 0.005.
   * @throws SQLException if something goes wrong.
   */
  public static boolean updateItemPrice(BigDecimal price, int itemId, Context context) throws NullParameterException,
      InvalidPriceException, SQLException {
    // Need to check if itemId exists?
    if (price == null) {
      throw new NullParameterException();
    }
    price = price.setScale(2, BigDecimal.ROUND_HALF_UP);
    if (price.compareTo(new BigDecimal(0)) <= 0) {
      throw new InvalidPriceException();
    }
    DatabaseDriverAndroid db = new DatabaseDriverAndroid(context);
    boolean complete = false;
    try{
      complete = db.updateItemPrice(price, itemId);
    }catch(Exception e){
      throw new SQLException();
    }
    return complete;
  }
  
  /**
   * Update the inventory quantity of the item associated with itemId.
   * @param quantity the item's quantity, must have quantity >=0.
   * @param itemId the id of the item.
   * @return true if the update is successful, otherwise false.
   * @throws InvalidQuantityException if quantity < 0.
   * @throws SQLException if something goes wrong.
   */
  public static boolean updateInventoryQuantity(int quantity, int itemId, Context context) throws InvalidQuantityException, SQLException {
    // Need to check if itemId exists?
    if (quantity < 0) {
      throw new InvalidQuantityException();
    }
    boolean complete = false;
    DatabaseDriverAndroid db = new DatabaseDriverAndroid(context);

    try{
      complete = db.updateInventoryQuantity(quantity, itemId);
    }catch (Exception e){
      throw new SQLException();
    }
    return complete;
  }
  //******************************************************************************************
  /**
   * remove items in database
 * @throws SQLException 
   */
  //public static boolean removeStoredAccountItem(int accountId, int itemId) throws SQLException {
	  //Connection connection = DatabaseDriverHelper.connectOrCreateDataBase();
	  //boolean complete = DatabaseUpdater.removeStoredAccountItem(accountId, itemId, connection);
	  //connection.close();
	  //return complete;
  //}
  //*******************************************************************************************
  ///**
   //* update the user's password
   //* @param newPass the new password
   //* @param userId user's ID
   //* @return true if the update is successful, otherwise false.
   //* @throws NullParameterException if the new password is null
   //* @throws SQLException if something goes wrong.
   //*/
  //public static boolean updateUserPassword(String newPass, int userId) throws NullParameterException, SQLException{
	  //if (newPass == null) {
	      //throw new NullParameterException();
	    //}
	    //if (newPass.length() > 64) {
	      //newPass = newPass.substring(0, 64);
	    //}
	    //Connection connection = DatabaseDriverHelper.connectOrCreateDataBase();
	    //boolean complete = DatabaseUpdater.updateUserPassword(newPass, userId, connection);
	    //connection.close();
	    //return complete;

  //}
  
  public static boolean updateAccountStatus(int accountId, boolean active, Context context) throws SQLException{
	  boolean result = false;
	  DatabaseDriverAndroid db = new DatabaseDriverAndroid(context);
	  try{
	    result = db.updateAccountStatus(accountId,active);
      }catch (Exception e){
	    throw new SQLException();
    }
	  return result;
  }
}
