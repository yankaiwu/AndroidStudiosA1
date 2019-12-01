package com.b07.database.helper;

import android.content.Context;
import android.database.Cursor;

import com.b07.exceptions.InvalidItemException;
import com.b07.exceptions.InvalidRoleException;
import com.b07.exceptions.ItemNotFoundException;
import com.b07.exceptions.NullParameterException;
import com.b07.exceptions.SaleNotFoundException;
import com.b07.exceptions.UserNotFoundException;
import com.b07.inventory.Inventory;
import com.b07.inventory.InventoryImpl;
import com.b07.inventory.Item;
import com.b07.inventory.ItemImpl;
import com.b07.store.Sale;
import com.b07.store.SaleImpl;
import com.b07.store.SalesLog;
import com.b07.store.SalesLogImpl;
import com.b07.users.Account;
import com.b07.users.Admin;
import com.b07.users.Customer;
import com.b07.users.Employee;
import com.b07.users.User;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;


public class DatabaseSelectHelper{

	public static boolean UserIDExists(int userId, Context context){
		DatabaseDriverAndroid db = new DatabaseDriverAndroid(context);
		Cursor cursor = db.getUsersDetails();
		while (cursor.moveToNext()){
			Integer tempId = cursor.getInt(cursor.getColumnIndex("ID"));
			if (tempId.equals(userId)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Get a list of all the roles in the database.
	 * @return a List<Integer> containing the ids from the roles table.
	 * @throws SQLException if something goes wrong.
	 */
	public static List<Integer> getRoleIds(Context context) throws SQLException {
		DatabaseDriverAndroid db = new DatabaseDriverAndroid(context);
		Cursor cursor = db.getRoles();
		List<Integer> ids = new ArrayList<>();
		while (cursor.moveToNext()) {
			ids.add(cursor.getInt(cursor.getColumnIndex("ID")));
		}
		cursor.close();
		return ids;
	}

	/**
	 * Get the name associated with roleId.
	 * @param roleId the roleId of the user.
	 * @return the name associated with roleId.
	 * @throws SQLException if something goes wrong.
	 */
	public static String getRoleName(int roleId, Context context) throws
			SQLException, InvalidRoleException {
		DatabaseDriverAndroid db = new DatabaseDriverAndroid(context);
		List<Integer> ids = getRoleIds(context);
		if (ids.contains(roleId)){
			String role = db.getRole(roleId);
			return role;
		} else {
			throw new InvalidRoleException();
		}
	}

	/**
	 * get the id associated with a role's name.
	 * @param name the name of the role.
	 * @param connection the database connection
	 * @return the id associated with a role.
	 * @throws SQLException thrown when something goes wrong with query.
	 */
	//public static int getRoleIdByName(String name, Context context) throws SQLException {
		//DatabaseDriverAndroid db = new DatabaseDriverAndroid(context);
		//int roleId = DatabaseSelector.getRoleIdByName(name, connection);
		//connection.close();
		//return roleId;
	//}

	/**
	 * Get the role id associated with userId.
	 * @param userId the id of the user.
	 * @return the role id associated with userId.
	 * @throws SQLException if something goes wrong.
	 */
	public static int getUserRoleId(int userId, Context context) throws UserNotFoundException {
		if (UserIDExists(userId, context)) {
			DatabaseDriverAndroid db = new DatabaseDriverAndroid(context);
			int roleId = db.getUserRole(userId);
			return roleId;
		} else {
			throw new UserNotFoundException();
		}
	}

	/**
	 * Get a list with all the users associated with roleId.
	 * @param roleId an id associated with a role.
	 * @return the role id associated with userId.
	 * @throws SQLException if something goes wrong.
	 */
	public static List<Integer> getUsersByRole(int roleId) throws SQLException {
		Connection connection = DatabaseDriverHelper.connectOrCreateDataBase();
		ResultSet results = DatabaseSelector.getUsersByRole(roleId, connection);
		List<Integer> userIds = new ArrayList<>();
		while (results.next()) {
			userIds.add(results.getInt("USERID"));
		}
		results.close();
		connection.close();
		return userIds;
	}

	/**
	 * Get a list with all users in the database.
	 * @return a list of all users in the database.
	 * @throws SQLException if something goes wrong.
	 */ 
	public static List<User> getUsersDetails(Context context) throws SQLException, InvalidRoleException {
		DatabaseDriverAndroid db = new DatabaseDriverAndroid(context);
		Cursor cursor = db.getUsersDetails();
		List<User> users = new ArrayList<>();
		int userId;
		String name;
		int age;
		String address;

		while (cursor.moveToNext()) {
			userId = cursor.getInt(cursor.getColumnIndex("ID"));
			name = cursor.getString(cursor.getColumnIndex("NAME"));
			age = cursor.getInt(cursor.getColumnIndex("AGE"));
			address = cursor.getString(cursor.getColumnIndex("ADDRESS"));
			switch (getRoleName(getUserRoleId(userId, context), context)) {
			case "ADMIN":
				users.add(new Admin(userId, name, age, address));
				break;
			case "EMPLOYEE":
				users.add(new Employee(userId, name, age, address));
				break;
			case "CUSTOMER":
				users.add(new Customer(userId, name, age, address));
				break;
			default:
				// Hopefully never reaches here
				System.out.println("Warning! User without a valid role is present in database.");
			}
		}
		cursor.close();
		return users;
	}

	/**
	 * Get information about the user associated with userId.
	 * @param userId the id of the user.
	 * @return information about the user associated with userId.
	 * @throws SQLException if something goes wrong.
	 * @throws UserNotFoundException if user with userId is not found in the database.
	 */
	public static User getUserDetails(int userId, Context context) throws
			InvalidRoleException, SQLException, UserNotFoundException {
		// Get Kevin's Code
		DatabaseDriverAndroid db = new DatabaseDriverAndroid(context);
		Cursor cursor = db.getUsersDetails();
		boolean flag = false;
		User user = null;

		while (cursor.moveToNext()) {
			if (cursor.getInt(cursor.getColumnIndex("ID")) == userId) {
				flag = true;
				break;
			}
		} if (flag) {
			String name = cursor.getString(cursor.getColumnIndex("NAME"));
			int age = cursor.getInt(cursor.getColumnIndex("AGE"));
			String address = cursor.getString((cursor.getColumnIndex("ADDRESS")));
			switch (getRoleName(getUserRoleId(userId, context), context)) {
			case "ADMIN":
				user = new Admin(userId, name, age, address);
				break;
			case "EMPLOYEE":
				user = new Employee(userId, name, age, address);
				break;
			case "CUSTOMER":
				user = new Customer(userId, name, age, address);
				break;
			default:
				// Hopefully never reaches here
				System.out.println("Warning! User without a valid role is present in database.");
			}
		} else {
			throw new UserNotFoundException();
		}
		cursor.close();
		return user;
	}

	/**
	 * Retrieve the password associated with userId from the database in hashed form.
	 * @param userId the id of the user.
	 * @return the password associated with userId (hashed)
	 * @throws SQLException if something goes wrong.
	 */
	public static String getPassword(int userId) throws SQLException {
		Connection connection = DatabaseDriverHelper.connectOrCreateDataBase();
		String password = DatabaseSelector.getPassword(userId, connection);
		connection.close();
		return password;
	}

	/**
	 * Get a list with all items stored in the database.
	 * @return a list with all items stored in the database.
	 * @throws SQLException if something goes wrong.
	 */
	public static List<Item> getAllItems() throws SQLException {
		Connection connection = DatabaseDriverHelper.connectOrCreateDataBase();
		ResultSet results = DatabaseSelector.getAllItems(connection);
		List<Item> items = new ArrayList<>();

		while(results.next()) {
			items.add(new ItemImpl(results.getInt("ID"), results.getString("Name"),
					new BigDecimal(results.getString("Price"))));
		}
		results.close();
		connection.close();
		return items;
	}

	/**
	 * Get information about the item associated with itemId.
	 * @param itemId the id of the item.
	 * @return information about the item associated with itemId.
	 * @throws SQLException if something goes wrong.
	 * @throws ItemNotFoundException if item with itemId is not found in the database.
	 */
	public static Item getItem(int itemId) throws SQLException, ItemNotFoundException {
		Connection connection = DatabaseDriverHelper.connectOrCreateDataBase();
		ResultSet results = DatabaseSelector.getItem(itemId, connection);
		boolean flag = false;
		Item item = null;

		while (results.next()) {
			if (results.getInt("ID") == itemId) {
				flag = true;
				break;
			}
		} if (flag) {
			item = new ItemImpl(results.getInt("ID"), results.getString("NAME"),
					new BigDecimal(results.getString("Price")));
		} else {
			throw new ItemNotFoundException();
		}
		results.close();
		connection.close();
		return item;
	}

	/**
	 * Get information about the itemId associated with an item.
	 * @param name the name of the item.
	 * @return the itemId associated with item.
	 * @throws SQLException if something goes wrong.
	 */
	public static int getItemId(String name) throws SQLException {
		Connection connection = DatabaseDriverHelper.connectOrCreateDataBase();
		int itemId = DatabaseSelector.getItemId(name, connection);
		connection.close();
		return itemId;
	}

	/**
	 * Get information about the stored inventory.
	 * @return information about the stored inventory.
	 * @throws SQLException if something goes wrong.
	 * @throws ItemNotFoundException if the database contains an item not in the database...
	 */
	public static Inventory getInventory() throws SQLException, ItemNotFoundException {
		// Okay, I admit this smells a little...
		return new InventoryImpl();
	}

	/**
	 * Get information about the stored inventory.
	 * @return a hashmap containing items as keys and quantities as values.
	 * @throws SQLException if something goes wrong.
	 * @throws ItemNotFoundException if the database contains an item not in the database...
	 */
	public static HashMap<Item, Integer> getInventoryHashMap() throws SQLException, ItemNotFoundException {
		Connection connection = DatabaseDriverHelper.connectOrCreateDataBase();
		ResultSet results = DatabaseSelector.getInventory(connection);
		HashMap<Item, Integer> itemMap = new HashMap<Item, Integer>();

		while(results.next()) {
			itemMap.put(getItem(results.getInt("ITEMID")), results.getInt("QUANTITY"));
		}
		results.close();
		connection.close();
		return itemMap;
	}

	/**
	 * Get the quantity of itemId stored in the inventory.
	 * @param itemId the id of the item.
	 * @return the quantity of itemId stored in the inventory.
	 * @throws SQLException if something goes wrong.
	 */
	public static int getInventoryQuantity(int itemId) throws SQLException {
		Connection connection = DatabaseDriverHelper.connectOrCreateDataBase();
		int quantity = DatabaseSelector.getInventoryQuantity(itemId, connection);
		connection.close();
		return quantity;
	}

	/**
	 * Get information about all sales.
	 * @return a SalesLog containing information about all sales.
	 * @throws SQLException if something goes wrong.
	 * @throws NullParameterException if we tried to insert null into our sales log.
	 * @throws UserNotFoundException if the sales table contains a sale by a user not in the users table.
	 */
	public static SalesLog getSales() throws SQLException, NullParameterException, UserNotFoundException {
		Connection connection = DatabaseDriverHelper.connectOrCreateDataBase();
		ResultSet results = DatabaseSelector.getSales(connection);
		SalesLog salesLog = new SalesLogImpl();
		Sale singleSale = new SaleImpl();

		while(results.next()) {
			singleSale.setId(results.getInt("ID"));
			singleSale.setUser(getUserDetails(results.getInt("USERID")));
			singleSale.setTotalPrice(new BigDecimal(results.getString("TOTALPRICE")));
			salesLog.addSale(singleSale);
		}
		results.close();
		connection.close();
		return salesLog;
	}

	/**
	 * Get information about a single sale associated with saleId.
	 * @param saleId the id of the sale.
	 * @return information about a single sale associated with saleId.
	 * @throws SQLException if something goes wrong.
	 * @throws SaleNotFoundException if no sale with saleId is found in the database.
	 * @throws UserNotFoundException if the sales table contains a sale by a user not in the users table.
	 */
	public static Sale getSaleById(int saleId) throws SaleNotFoundException, SQLException, UserNotFoundException {
		Connection connection = DatabaseDriverHelper.connectOrCreateDataBase();
		ResultSet results = DatabaseSelector.getSaleById(saleId, connection);
		Sale singleSale = new SaleImpl();
		boolean flag = false;

		while(results.next()) {
			if (results.getInt("ID") == saleId) {
				flag = true;
				break;
			}
		} if (flag) {
			singleSale.setId(results.getInt("ID"));
			singleSale.setUser(getUserDetails(results.getInt("USERID")));
			singleSale.setTotalPrice(new BigDecimal(results.getString("TOTALPRICE")));
		} else {
			throw new SaleNotFoundException();
		}
		results.close();
		connection.close();
		return singleSale;
	}

	/**
	 * Get information about all sales associated with userId.
	 * @param userId the id of the user.
	 * @return a list of all sales associated with userId.
	 * @throws SQLException if something goes wrong.
	 * @throws UserNotFoundException if userId is not in the users table.
	 */
	public static List<Sale> getSalesToUser(int userId) throws SQLException, UserNotFoundException {
		Connection connection = DatabaseDriverHelper.connectOrCreateDataBase();
		ResultSet results = DatabaseSelectHelper.getSalesToUser(userId, connection);
		User user = getUserDetails(userId);
		List<Sale> sales = new ArrayList<>();
		Sale singleSale = null;

		while (results.next()) {
			if (results.getInt("USERID") == userId) {
				singleSale = new SaleImpl();
				singleSale.setId(results.getInt("ID"));
				singleSale.setUser(user);
				singleSale.setTotalPrice(new BigDecimal(results.getString("TOTALPRICE")));
			}
		}
		results.close();
		connection.close();
		return sales;
	}

	/**
	 * Get information about all items associated with saleId.
	 * @param saleId the id of the sale.
	 * @return information about saleId including information about items.
	 * @throws SQLException if something goes wrong.
	 * @throws UserNotFoundException if saleId is associated with a user not in the users table.
	 * @throws SaleNotFoundException if no sale with saleId is found in the database.
	 * @throws ItemNotFoundException if the itemizedsales table contains an item not in the items table.
	 */
	public static Sale getItemizedSaleById(int saleId) throws SQLException, SaleNotFoundException,
	UserNotFoundException, ItemNotFoundException {
		Connection connection = DatabaseDriverHelper.connectOrCreateDataBase();
		ResultSet results = DatabaseSelector.getItemizedSaleById(saleId, connection);
		Sale itemizedSale = getSaleById(saleId);
		HashMap<Item, Integer> itemMap = new HashMap<Item, Integer>();

		while (results.next()) {
			if (results.getInt("SALEID") == saleId) {
				itemMap.put(getItem(results.getInt("ITEMID")), results.getInt("QUANTITY"));
			}
		}
		itemizedSale.setItemMap(itemMap);
		results.close();
		connection.close();
		return itemizedSale;
	}

	/**
	 * Get information about all items associated with all sales.
	 * @return a SalesLog containing information about all sales including information about items.
	 * @throws SQLException if something goes wrong.
	 * @throws UserNotFoundException if the sales table contains a sale by a user not in the users table.
	 * @throws SaleNotFoundException if no sale with saleId is found in the database.
	 * @throws ItemNotFoundException if the itemizedsales table contains an item not in the items table. 
	 * @throws NullParameterException if getItemizedSaleById returns null.
	 */
	public static SalesLog getItemizedSales() throws SQLException, NullParameterException, SaleNotFoundException,
	UserNotFoundException, ItemNotFoundException {
		Connection connection = DatabaseDriverHelper.connectOrCreateDataBase();
		ResultSet results = DatabaseSelector.getItemizedSales(connection);
		HashSet<Integer> saleIdSet = new HashSet<Integer>();

		while (results.next()) {
			saleIdSet.add(results.getInt("SALEID"));
		}
		SalesLog salesLog = new SalesLogImpl();
		for (int saleId : saleIdSet) {
			salesLog.addSale(getItemizedSaleById(saleId));
		}

		results.close();
		connection.close();
		return null;
	}
	public static int getUserAccounts(int userId) throws SQLException {
		Connection connection = DatabaseDriverHelper.connectOrCreateDataBase();
		ResultSet results = DatabaseSelector.getUserAccounts(userId, connection);
		results.next();

		int accountId = results.getInt("ID");


		results.close();
		connection.close();
		return accountId;

	}

	// NOTE: 
	public static HashMap<Item, Integer> getAccountDetails(int accountId) throws SQLException, ItemNotFoundException {
		Connection connection = DatabaseDriverHelper.connectOrCreateDataBase();
		ResultSet results = DatabaseSelector.getAccountDetails(accountId, connection);
		HashMap<Item, Integer> items = new HashMap<Item, Integer>();
		while (results.next()) {
			int itemId = results.getInt("ITEMID");
			Item item = getItem(itemId);
			int quantity = results.getInt("QUANTITY");
			items.put(item, quantity);
		}

		results.close();
		connection.close();
		return items;
	}
	/**
	 * 
	 * @return all accounts in the database
	 * @throws SQLException
	 * @throws InvalidItemException
	 * @throws ItemNotFoundException 
	 */
	public static List<Account> getAllAccounts()
			throws SQLException,InvalidItemException, ItemNotFoundException {
		List<Account> accountList = new ArrayList<Account>();
		Connection connection = DatabaseDriverHelper.connectOrCreateDataBase();
		ResultSet results = DatabaseSelector.getAllAccount(connection);
		while (results.next()) {
			int accountId = results.getInt("ID");
			int userId = results.getInt("USERID");
			int activeValue = results.getInt("ACTIVE");
			int approveValue = results.getInt("APPROVE");
			Account account = (Account) getAccountDetails(accountId);
			account.setUserId(userId);
			if (activeValue == 0) {
				account.setActive(false);
			} else {
				account.setActive(true);
			}
			if (approveValue == 0) {
				account.setApprove(false);
			} else {
				account.setApprove(true);
			}
			accountList.add(account);
		}
		results.close();
		connection.close();
		return accountList;
	}
	
	public static List<Account> getUserActiveAccounts(int userId) throws SQLException, ItemNotFoundException, UserNotFoundException{
		List<Account> listOfActive = new ArrayList<Account>();
		Connection connection = DatabaseDriverHelper.connectOrCreateDataBase();
		ResultSet results = DatabaseSelector.getUserActiveAccounts(userId, connection);
		while(results.next()) {
			int accountId = results.getInt("ID");
			Account userAccount = (Account) getAccountDetails(accountId);
			userAccount.setUser(getUserDetails(userId));
			userAccount.setUserId(userId);
			userAccount.setActive(true);
			listOfActive.add(userAccount);
			
		}
		results.close();
		connection.close();
		return listOfActive;
	}
	
	public static List<Account> getUserInactiveAccounts(int userId) throws SQLException, ItemNotFoundException, UserNotFoundException{
		List<Account> listOfInactive = new ArrayList<Account>();
		Connection connection = DatabaseDriverHelper.connectOrCreateDataBase();
		ResultSet results = DatabaseSelector.getUserActiveAccounts(userId, connection);
		while(results.next()) {
			int accountId = results.getInt("ID");
			Account userAccount = (Account) getAccountDetails(accountId);
			userAccount.setUser(getUserDetails(userId));
			userAccount.setUserId(userId);
			userAccount.setActive(false);
			listOfInactive.add(userAccount);
			
		}
		results.close();
		connection.close();
		return listOfInactive;
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}
