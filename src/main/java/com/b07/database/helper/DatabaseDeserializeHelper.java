package com.b07.database.helper;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import com.b07.exceptions.ConnectionFailedException;
import com.b07.exceptions.DatabaseInsertException;
import com.b07.exceptions.InvalidItemException;
import com.b07.exceptions.InvalidPriceException;
import com.b07.exceptions.InvalidQuantityException;
import com.b07.exceptions.InvalidRoleException;
import com.b07.exceptions.ItemNotFoundException;
import com.b07.exceptions.NullParameterException;
import com.b07.exceptions.SaleNotFoundException;
import com.b07.exceptions.UserNotFoundException;
import com.b07.inventory.Inventory;
import com.b07.inventory.Item;
import com.b07.store.Sale;
import com.b07.store.SalesLog;
import com.b07.users.Account;
import com.b07.users.User;

public class DatabaseDeserializeHelper {
	/**
	 * This method is to help de-serialize list of Account and User object 
	 * @param connection the link made to database
	 * @return true if this operation is success, false otherwise
	 * @throws IOException 
	 * @throws ClassNotFoundException 
	 * @throws SQLException 
	 * @throws ConnectionFailedException 

	 */
	public static boolean deserializeHelper(Connection connection, String directoryofFile) throws IOException, ClassNotFoundException, SQLException, ConnectionFailedException {
		boolean restored = false;
		String file = directoryofFile + "database_copy.ser";
		FileInputStream fileIn = new FileInputStream(file);
		ObjectInputStream in = new ObjectInputStream(fileIn);
		List<Account> userAccount = new ArrayList<Account>();
		List<User> userList = new ArrayList<User>();
		int size = in.readInt();
		for (int i = 0; i < size; i++) {
			User user = (User) in.readObject();
			userList.add(user);
		}
		int accountSize = in.readInt();
		for (int i = 0; i < accountSize; i++) {
			Account account = (Account) in.readObject();
			userAccount.add(account);
		}
		int itemSize = in.readInt();
		List<Item> items = new ArrayList<Item>();
		for (int i = 0; i < itemSize; i++) {
			Item item = (Item) in.readObject();
			items.add(item);
		}
		HashMap<Integer, String> roleNames = new HashMap<Integer, String>();
		int roleSize = in.readInt();
		for (int i = 0; i < roleSize; i++) {
			Integer roleId = (Integer) in.readObject();
			String roleName = (String) in.readObject();
			roleNames.put(roleId, roleName);
		}
		HashMap<User, String> userPassword = new HashMap<User, String>();
		int userSize = in.readInt();
		for (int i = 0; i < userSize; i++) {
			User user = (User) in.readObject();
			String password = (String) in.readObject();
			userPassword.put(user, password);
		}
		List<Sale> saleList = new ArrayList<Sale>();
		int saleListSize = in.readInt();
		for (int i = 0; i < saleListSize; i++) {
			Sale sale = (Sale) in.readObject();
			saleList.add(sale);
		}
		Inventory inventory = (Inventory) in.readObject();
		connection.close();
		DatabaseDriverHelper.reInitialize();
		boolean restoreUser = restoreUser(userPassword, roleNames, userList);
		boolean restoreInventoryAndItem = restoreInventoryAndItem(inventory, items);
		boolean restoreSales = restoreSale(saleList);
		boolean restoreAccount = restoreAccounts(userAccount);
		in.close();
		fileIn.close();
		restored = restoreUser && restoreInventoryAndItem && restoreSales && restoreAccount;

		return restored;
	}
	/**
	 * This method is to help restore the given list of Accounts by taking each account's id, approve status,
	 * activities and items to make new objects and insert to database.
	 * @param userAccounts the list of Accounts corresponds to each users
	 * @return true if this operation success
	 */
	private static boolean restoreAccounts(List<Account> userAccounts) {
		// TODO Auto-generated method stub
		try {
			for (Account account : userAccounts) {
				int accountId =
						DatabaseInsertHelper.insertAccount(account.getUserId(), account.getActive());
				HashMap<Item, Integer> itemInCart = account.getShoppingCart().getInformation();
				for (Item item : itemInCart.keySet()) {
					DatabaseInsertHelper.insertAccountLine(accountId, item.getId(), itemInCart.get(item));
				}
			}
			return true;
		} catch (SQLException | DatabaseInsertException e) {
			System.out.println("Due to some error from the data in ser file, opeation failed");
		}

		return false;
	}


	/**
	 * This method is to restore a list of Sales by re-inserting each of them to database
	 * @param saleList the list of Sale needs to be restored
	 * @return true if this operation success
	 */
	private static boolean restoreSale(List<Sale> saleList) {
		// TODO Auto-generated method stub
		try {
			for (Sale sale : saleList) {
				int saleId = DatabaseInsertHelper.insertSale(sale.getUser().getId(), sale.getTotalPrice());
				HashMap<Item, Integer> saleItems = sale.getItemMap();
				for (Item item : saleItems.keySet()) {
					DatabaseInsertHelper.insertItemizedSale(saleId, item.getId(), saleItems.get(item));
				}
			}
			return true;
		} catch (SQLException | InvalidPriceException | DatabaseInsertException | ItemNotFoundException | InvalidQuantityException | SaleNotFoundException | UserNotFoundException | NullParameterException e) {
			System.out.println("Due to some error from the data in ser file, opeation failed");
		}
		return false;
	}

	/**
	 * This method is to help restore all the items in given inventory.
	 * @param currentInventory the inventory wants to get restored
	 * @param items the list of items in the inventory
	 * @return true if the operation is success
	 */
	private static boolean restoreInventoryAndItem(Inventory currentInventory, List<Item> items) {
		try {
			for (Item item : items) {
				DatabaseInsertHelper.insertItem(item.getName(), item.getPrice());
			}
			HashMap<Item, Integer> inventory = currentInventory.getItemMap();
			for (Item item : inventory.keySet()) {
				DatabaseInsertHelper.insertInventory(item.getId(), inventory.get(item));
			}
			return true;
		} catch (SQLException | DatabaseInsertException | InvalidItemException | NullParameterException | InvalidPriceException | InvalidQuantityException | ItemNotFoundException e) {
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * This method is to restore the given list of users by taking its userId match to password,
	 * roleName match to roleId.
	 * @param userPassword the HashMap matches user to its password
	 * @param roleNames the hashMap matches roleId to its roleName
	 * @param userList the list of users needs to be restored
	 * @return
	 */
	private static boolean restoreUser(HashMap<User, String> userPassword,
			HashMap<Integer, String> roleNames, List<User> userList) {
		try {
			for (User user : userList) {
				String password = userPassword.get(user);
				int userId;
				userId = DatabaseInsertHelper.insertNewUser(user.getName(), user.getAge(),
						user.getAddress(), "0");
				String roleName = roleNames.get(user.getRoleId());
				int roleId = DatabaseInsertHelper.insertRole(roleName);
				DatabaseInsertHelper.insertUserRole(userId, roleId);
				DatabaseUpdateHelper.updateUserPassword(password, user.getId());
			}
			return true;
		} catch (SQLException | DatabaseInsertException | NullParameterException | InvalidRoleException | UserNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}
}
