package com.b07.users;

import java.io.Serializable;
import java.sql.SQLException;
import java.util.HashMap;

import com.b07.database.helper.DatabaseInsertHelper;
import com.b07.database.helper.DatabaseSelectHelper;
import com.b07.database.helper.DatabaseUpdateHelper;
import com.b07.exceptions.DatabaseInsertException;
import com.b07.exceptions.InvalidItemException;
import com.b07.exceptions.InvalidQuantityException;
import com.b07.exceptions.ItemNotFoundException;
import com.b07.exceptions.NullParameterException;
import com.b07.inventory.Item;
import com.b07.store.ShoppingCart;

public class Customer extends User implements Serializable {
	private static final long serialVersionUID = 4829072750856749859L;
	private transient boolean authenticated = false;

	public Customer(int id, String name, int age, String address) {
		super.setId(id);
		super.setName(name);
		super.setAge(age);
		super.setAddress(address);
	}

	public Customer(int id, String name, int age, String address, boolean authenticated) {
		super.setId(id);
		super.setName(name);
		super.setAge(age);
		super.setAddress(address);
		this.authenticated = true;
	}

	public void saveCurrentShoppingCart(ShoppingCart customerCart) throws SQLException, DatabaseInsertException,
	ItemNotFoundException {
		int accountId = DatabaseSelectHelper.getUserAccounts(getId());

		HashMap<Item, Integer> itemsInDatabase = DatabaseSelectHelper.getAccountDetails(accountId);

		for (Item itemToBeDeleted : itemsInDatabase.keySet()) {
			int itemId = itemToBeDeleted.getId();
			DatabaseUpdateHelper.removeStoredAccountItem(accountId, itemId);
		}

		for (Item item : customerCart.getItems()) {
			int itemId = item.getId();
			int quantityInCart = customerCart.getQuantity(item);
			DatabaseInsertHelper.insertAccountLine(accountId, itemId, quantityInCart);
		}
	}

	public void loadPreviousShoppingCart(ShoppingCart customerCart) throws SQLException, ItemNotFoundException,
	NullParameterException, InvalidItemException, InvalidQuantityException{
		int accountId = DatabaseSelectHelper.getUserAccounts(getId());
		HashMap<Item, Integer> previousCart = DatabaseSelectHelper.getAccountDetails(accountId);
		customerCart.clearCart();

		for(Item item: previousCart.keySet()){
			customerCart.addItem(item, previousCart.get(item));
		}
	}
}
