package com.b07.users;

import android.content.Context;

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
import com.b07.inventory.Inventory;
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

	public void saveCurrentShoppingCart(ShoppingCart customerCart, Context context)
			throws DatabaseInsertException {
		int accountId = DatabaseInsertHelper.insertAccount(customerCart.getCustomer().getId(),
				true, context);
		for (Item item : customerCart.getItems()) {
			int itemId = item.getId();
			int quantityInCart = customerCart.getQuantity(item);
			DatabaseInsertHelper.insertAccountLine(accountId, itemId, quantityInCart, context);
		}
	}

	public void loadPreviousShoppingCart(ShoppingCart customerCart, int accountId, Context context)
			throws ItemNotFoundException, NullParameterException, InvalidItemException, InvalidQuantityException{
		HashMap<Item, Integer> previousCart = DatabaseSelectHelper.getAccountDetails(accountId,
				context);
		customerCart.clearCart();

		for(Item item: previousCart.keySet()){
			customerCart.addItem(item, previousCart.get(item));
		}
	}
	
	public Inventory itemSearch(String query, Context context) throws NullParameterException,
			ItemNotFoundException {
	  return DatabaseSelectHelper.getItemsByQuery(query, context);
	}
}
