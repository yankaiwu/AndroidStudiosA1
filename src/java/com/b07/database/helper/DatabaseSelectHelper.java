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
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;


public class DatabaseSelectHelper{

	public static boolean UserIdExists(int userId, Context context) {
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
	public static List<Integer> getRoleIds(Context context) {
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
	public static String getRoleName(int roleId, Context context) throws InvalidRoleException,
			NullParameterException {
		if (context == null) {
			throw new NullParameterException();
		}
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
	 * @return the id associated with a role, -1 if role is not found.
	 */
	public static int getRoleIdByName(String name, Context context){
		DatabaseDriverAndroid db = new DatabaseDriverAndroid(context);
		Cursor cursor = db.getRoles();
		int roleId = -1;
		while (cursor.moveToNext()){
			if (cursor.getString(cursor.getColumnIndex("NAME")).equalsIgnoreCase(name)){
				roleId = cursor.getInt(cursor.getColumnIndex("ID"));
			}
		}
		cursor.close();
		return roleId;
	}

	/**
	 * Get the role id associated with userId.
	 * @param userId the id of the user.
	 * @return the role id associated with userId.
	 * @throws SQLException if something goes wrong.
	 */
	public static int getUserRoleId(int userId, Context context) {
		DatabaseDriverAndroid db = new DatabaseDriverAndroid(context);
		int roleId = db.getUserRole(userId);
		return roleId;
	}

	/**
	 * Get a list with all the users associated with roleId.
	 * @param roleId an id associated with a role.
	 * @return the role id associated with userId.
	 * @throws SQLException if something goes wrong.
	 */
	public static List<Integer> getUsersByRole(int roleId, Context context) {
		DatabaseDriverAndroid db = new DatabaseDriverAndroid(context);
		Cursor cursor = db.getUsersByRole(roleId);
		List<Integer> userIds = new ArrayList<>();

		while (cursor.moveToNext()) {
			userIds.add(cursor.getInt(cursor.getColumnIndex("USERID")));
		}
		cursor.close();
		return userIds;
	}

	/**
	 * Get a list with all users in the database.
	 * @return a list of all users in the database.
	 * @throws SQLException if something goes wrong.
	 */ 
	public static List<User> getUsersDetails(Context context) throws
			UserNotFoundException, InvalidRoleException, NullParameterException {
		if (context == null) {
			throw new NullParameterException();
		}
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
	 * @throws UserNotFoundException if user with userId is not found in the database.
	 */
	public static User getUserDetails(int userId, Context context) throws
			InvalidRoleException, UserNotFoundException, NullParameterException {
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
	public static String getPassword(int userId, Context context) {
		DatabaseDriverAndroid db = new DatabaseDriverAndroid(context);
		return db.getPassword(userId);
	}

	/**
	 * Get a list with all items stored in the database.
	 * @return a list with all items stored in the database.
	 * @throws SQLException if something goes wrong.
	 */
	public static List<Item> getAllItems(Context context) throws NullParameterException {
		if (context == null) {
			throw new NullParameterException();
		}
		DatabaseDriverAndroid db = new DatabaseDriverAndroid(context);
		Cursor cursor = db.getAllItems();
		List<Item> items = new ArrayList<>();

		while(cursor.moveToNext()) {
			int itemId = cursor.getInt(cursor.getColumnIndex("ID"));
			String name = cursor.getString(cursor.getColumnIndex("NAME"));
			String price = cursor.getString(cursor.getColumnIndex("PRICE"));
			items.add(new ItemImpl(itemId, name, new BigDecimal(price)));
		}
		cursor.close();
		return items;
	}

	/**
	 * Get information about the item associated with itemId.
	 * @param itemId the id of the item.
	 * @return information about the item associated with itemId.
	 * @throws SQLException if something goes wrong.
	 * @throws ItemNotFoundException if item with itemId is not found in the database.
	 */
	public static Item getItem(int itemId, Context context) throws ItemNotFoundException,
			NullParameterException {
		if (context == null) {
			throw new NullParameterException();
		}
		DatabaseDriverAndroid db = new DatabaseDriverAndroid(context);
		Cursor cursor = db.getItem(itemId);

		boolean flag = false;
		Item item;

		while (cursor.moveToNext()) {
			if (cursor.getInt(cursor.getColumnIndex("ID")) == itemId) {
				flag = true;
				break;
			}
		} if (flag) {
			String name = cursor.getString(cursor.getColumnIndex("NAME"));
			String price = cursor.getString(cursor.getColumnIndex("PRICE"));
			item = new ItemImpl(itemId, name, new BigDecimal(price));
		} else {
			throw new ItemNotFoundException();
		}
		cursor.close();
		return item;
	}

	/**
	 * Get information about the itemId associated with an item.
	 * @param name the name of the item.
	 * @return the itemId associated with item.
	 * @throws SQLException if something goes wrong.
	 */
	public static int getItemId(String name, Context context) throws NullParameterException,
			ItemNotFoundException {
		if (context == null) {
			throw new NullParameterException();
		}
		DatabaseDriverAndroid db = new DatabaseDriverAndroid(context);
		for (Item item : getAllItems(context)) {
			if (item.getName().equals(name)) {
				return item.getId();
			}
		}
		throw new ItemNotFoundException();
	}

	/**
	 * Get information about the stored inventory.
	 * @return information about the stored inventory.
	 * @throws SQLException if something goes wrong.
	 * @throws ItemNotFoundException if the database contains an item not in the database...
	 */
	public static Inventory getInventory(Context context) throws ItemNotFoundException,
			NullParameterException {
		if (context == null) {
			throw new NullParameterException();
		}
		DatabaseDriverAndroid db = new DatabaseDriverAndroid(context);
		Cursor cursor = db.getInventory();
		Inventory inventory = new InventoryImpl();

		while(cursor.moveToNext()) {
			Item item = getItem(cursor.getInt(cursor.getColumnIndex("ITEMID")), context);
			int quantity = cursor.getInt(cursor.getColumnIndex("QUANTITY"));
			inventory.updateMap(item, quantity);
		}
		cursor.close();
		return inventory;
	}

	/**
	 * Get the quantity of itemId stored in the inventory.
	 * @param itemId the id of the item.
	 * @return the quantity of itemId stored in the inventory.
	 * @throws SQLException if something goes wrong.
	 */
	public static int getInventoryQuantity(int itemId, Context context)
			throws NullParameterException {
		if (context == null) {
			throw new NullParameterException();
		}
		DatabaseDriverAndroid db = new DatabaseDriverAndroid(context);
		return db.getInventoryQuantity(itemId);
	}

	/**
	 * Get information about all sales.
	 * @return a SalesLog containing information about all sales.
	 * @throws SQLException if something goes wrong.
	 * @throws NullParameterException if we tried to insert null into our sales log.
	 * @throws UserNotFoundException if the sales table contains a sale by a user not in the users table.
	 */
	public static SalesLog getSales(Context context) throws NullParameterException,
			UserNotFoundException, InvalidRoleException {
		if (context == null) {
			throw new NullParameterException();
		}
		DatabaseDriverAndroid db = new DatabaseDriverAndroid(context);
		Cursor cursor = db.getSales();
		SalesLog salesLog = new SalesLogImpl();
		Sale singleSale = new SaleImpl();

		while(cursor.moveToNext()) {
			singleSale.setId(cursor.getInt(cursor.getColumnIndex("ID")));
			singleSale.setUser(getUserDetails(cursor.getInt(cursor.getColumnIndex("USERID")),
					context));
			singleSale.setTotalPrice(new BigDecimal(cursor.getString(cursor.getColumnIndex(
					"TOTALPRICE"))));
			salesLog.addSale(singleSale);
		}
		cursor.close();
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
	public static Sale getSaleById(int saleId, Context context) throws SaleNotFoundException,
			UserNotFoundException, NullParameterException, InvalidRoleException {
		if (context == null) {
			throw new NullParameterException();
		}
		DatabaseDriverAndroid db = new DatabaseDriverAndroid(context);
		Cursor cursor = db.getSaleById(saleId);
		Sale singleSale = new SaleImpl();
		boolean flag = false;

		while(cursor.moveToNext()) {
			if (cursor.getInt(cursor.getColumnIndex("ID")) == saleId) {
				flag = true;
				break;
			}
		} if (flag) {
			singleSale.setId(cursor.getInt(cursor.getColumnIndex("ID")));
			singleSale.setUser(getUserDetails(cursor.getInt(cursor.getColumnIndex("USERID")),
					context));
			singleSale.setTotalPrice(new BigDecimal(cursor.getString(cursor.getColumnIndex(
					"TOTALPRICE"))));
		} else {
			throw new SaleNotFoundException();
		}
		cursor.close();
		return singleSale;
	}

	/**
	 * Get information about all sales associated with userId.
	 * @param userId the id of the user.
	 * @return a list of all sales associated with userId.
	 * @throws SQLException if something goes wrong.
	 * @throws UserNotFoundException if userId is not in the users table.
	 */
	public static List<Sale> getSalesToUser(int userId, Context context)
			throws UserNotFoundException, NullParameterException, InvalidRoleException {
		if (context == null) {
			throw new NullParameterException();
		}
		DatabaseDriverAndroid db = new DatabaseDriverAndroid(context);
		Cursor cursor = db.getSalesToUser(userId);
		User user = getUserDetails(userId, context);
		List<Sale> sales = new ArrayList<>();
		Sale singleSale;

		while (cursor.moveToNext()) {
			if (cursor.getInt(cursor.getColumnIndex("USERID")) == userId) {
				singleSale = new SaleImpl();
				singleSale.setId(cursor.getInt(cursor.getColumnIndex("ID")));
				singleSale.setUser(user);
				singleSale.setTotalPrice(new BigDecimal(cursor.getString(cursor.getColumnIndex(
						"TOTALPRICE"))));
			}
		}
		cursor.close();
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
	public static Sale getItemizedSaleById(int saleId, Context context)
			throws SaleNotFoundException, UserNotFoundException, ItemNotFoundException,
			NullParameterException, InvalidRoleException {
		if (context == null) {
			throw new NullParameterException();
		}
		DatabaseDriverAndroid db = new DatabaseDriverAndroid(context);
		Cursor cursor = db.getItemizedSaleById(saleId);
		Sale itemizedSale = getSaleById(saleId, context);
		HashMap<Item, Integer> itemMap = new HashMap<Item, Integer>();

		while (cursor.moveToNext()) {
			if (cursor.getInt(cursor.getColumnIndex("SALEID")) == saleId) {
				int itemId = cursor.getInt(cursor.getColumnIndex("ITEMID"));
				int quantity = cursor.getInt(cursor.getColumnIndex("QUANTITY"));
				itemMap.put(getItem(itemId, context), quantity);
			}
		}
		itemizedSale.setItemMap(itemMap);
		cursor.close();
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
	public static SalesLog getItemizedSales(Context context) throws NullParameterException,
			SaleNotFoundException, UserNotFoundException, ItemNotFoundException,
			InvalidRoleException {
		if (context == null) {
			throw new NullParameterException();
		}
		DatabaseDriverAndroid db = new DatabaseDriverAndroid(context);
		Cursor cursor = db.getItemizedSales();
		HashSet<Integer> saleIdSet = new HashSet<Integer>();

		while (cursor.moveToNext()) {
			saleIdSet.add(cursor.getInt(cursor.getColumnIndex("SALEID")));
		}
		SalesLog salesLog = new SalesLogImpl();
		for (int saleId : saleIdSet) {
			salesLog.addSale(getItemizedSaleById(saleId, context));
		}

		cursor.close();
		return null;
	}
	public static List<Integer> getUserAccounts(int userId, Context context)
			throws NullParameterException {
		if (context == null) {
			throw new NullParameterException();
		}
		DatabaseDriverAndroid db = new DatabaseDriverAndroid(context);
		Cursor cursor = db.getUserAccounts(userId);
		ArrayList<Integer> userAccounts = new ArrayList<Integer>();

		while (cursor.moveToNext()) {
			userAccounts.add(cursor.getInt(cursor.getColumnIndex("ID")));
		}
		cursor.close();
		return userAccounts;

	}

	// NOTE: 
	public static HashMap<Item, Integer> getAccountDetails(int accountId, Context context)
			throws ItemNotFoundException, NullParameterException {
		if (context == null) {
			throw new NullParameterException();
		}
		DatabaseDriverAndroid db = new DatabaseDriverAndroid(context);
		Cursor cursor = db.getAccountDetails(accountId);
		HashMap<Item, Integer> items = new HashMap<Item, Integer>();
		while (cursor.moveToNext()) {
			int itemId = cursor.getInt(cursor.getColumnIndex("ITEMID"));
			Item item = getItem(itemId, context);
			int quantity = cursor.getInt(cursor.getColumnIndex("QUANTITY"));
			items.put(item, quantity);
		}
		cursor.close();
		return items;
	}
	/**
	 * 
	 * @return all accounts in the database
	 * @throws SQLException
	 * @throws InvalidItemException
	 * @throws ItemNotFoundException 
	 */
	public static List<Account> getAllAccounts(Context context) throws ItemNotFoundException,
			NullParameterException {
		if (context == null) {
			throw new NullParameterException();
		}
		DatabaseDriverAndroid db = new DatabaseDriverAndroid(context);
		Cursor cursor = db.getAllAccounts();
		List<Account> accountList = new ArrayList<Account>();

		while (cursor.moveToNext()) {
			int accountId = cursor.getInt(cursor.getColumnIndex("ID"));
			int userId = cursor.getInt(cursor.getColumnIndex("USERID"));
			int activeValue = cursor.getInt(cursor.getColumnIndex("ACTIVE"));
			int approveValue = cursor.getInt(cursor.getColumnIndex("APPROVE"));
			Account account = (Account) getAccountDetails(accountId, context);
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
		cursor.close();
		return accountList;
	}
	
	public static List<Account> getUserActiveAccounts(int userId, Context context)
			throws ItemNotFoundException, UserNotFoundException, NullParameterException,
			InvalidRoleException {
		if (context == null) {
			throw new NullParameterException();
		}
		DatabaseDriverAndroid db = new DatabaseDriverAndroid(context);
		List<Account> listOfActive = new ArrayList<Account>();
		Cursor cursor = db.getUserActiveAccounts(userId);

		while(cursor.moveToNext()) {
			int accountId = cursor.getInt(cursor.getColumnIndex("ID"));
			Account userAccount = (Account) getAccountDetails(accountId, context);
			userAccount.setUser(getUserDetails(userId, context));
			userAccount.setUserId(userId);
			userAccount.setActive(true);
			listOfActive.add(userAccount);
			
		}
		cursor.close();
		return listOfActive;
	}
	
	public static List<Account> getUserInactiveAccounts(int userId, Context context)
			throws ItemNotFoundException, UserNotFoundException, NullParameterException, InvalidRoleException {
		if (context == null) {
			throw new NullParameterException();
		}
		DatabaseDriverAndroid db = new DatabaseDriverAndroid(context);
		List<Account> listOfInactive = new ArrayList<Account>();
		Cursor cursor = db.getUserInactiveAccounts(userId);

		while(cursor.moveToNext()) {
			int accountId = cursor.getInt(cursor.getColumnIndex("ID"));
			Account userAccount = (Account) getAccountDetails(accountId, context);
			userAccount.setUser(getUserDetails(userId, context));
			userAccount.setUserId(userId);
			userAccount.setActive(false);
			listOfInactive.add(userAccount);
			
		}
		cursor.close();
		return listOfInactive;
	}

	public static HashSet<User> getUserLoginsByQuery(String query, Context context)
			throws UserNotFoundException, NullParameterException, InvalidRoleException {
		if (context == null) {
			throw new NullParameterException();
		}
		DatabaseDriverAndroid db = new DatabaseDriverAndroid(context);
		Cursor cursor = db.getUserLoginList();
		HashSet<User> loginList = new HashSet<User>();

		while(cursor.moveToNext()) {
			String login = cursor.getString(cursor.getColumnIndex("LOGIN"));
			if (login.toLowerCase().contains(query.toLowerCase())) {
				loginList.add(getUserDetails(cursor.getInt(cursor.getColumnIndex("USERID")),
						context));
			}
		}
		cursor.close();
		return loginList;
	}

	public static Inventory getItemsByQuery(String query, Context context)
			throws ItemNotFoundException, NullParameterException {
		if (context == null) {
			throw new NullParameterException();
		}
		DatabaseDriverAndroid db = new DatabaseDriverAndroid(context);
		Cursor cursor = db.getAllItems();
		Inventory inventory = new InventoryImpl();

		while(cursor.moveToNext()) {
			String name = cursor.getString(cursor.getColumnIndex("NAME"));
			if (name.toLowerCase().contains(query.toLowerCase())) {
				int itemId = cursor.getInt(cursor.getColumnIndex("ID"));
				inventory.updateMap(getItem(itemId, context), getInventoryQuantity(itemId, context));
			}
		}
		cursor.close();
		return inventory;
	}
}
