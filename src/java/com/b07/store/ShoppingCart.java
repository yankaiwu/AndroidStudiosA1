package com.b07.store;

import android.content.Context;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import com.b07.database.helper.DatabaseInsertHelper;
import com.b07.database.helper.DatabaseSelectHelper;
import com.b07.database.helper.DatabaseUpdateHelper;
import com.b07.exceptions.DatabaseInsertException;
import com.b07.exceptions.InvalidItemException;
import com.b07.exceptions.InvalidPriceException;
import com.b07.exceptions.InvalidQuantityException;
import com.b07.exceptions.InvalidSaleException;
import com.b07.exceptions.ItemNotFoundException;
import com.b07.exceptions.NullParameterException;
import com.b07.exceptions.SaleNotFoundException;
import com.b07.exceptions.UserNotFoundException;
import com.b07.inventory.Inventory;
import com.b07.inventory.Item;
import com.b07.inventory.ItemTypes;
import com.b07.users.Customer;

public class ShoppingCart {
  private HashMap<Item, Integer> items = new HashMap<Item, Integer>();
  private Customer customer;
  private BigDecimal total = new BigDecimal(0);
  private final BigDecimal TAXRATE = new BigDecimal(1.13);
  
  public ShoppingCart(Customer customer) {
    this.customer = customer;
  }
  
  public void addItem(Item item, int quantity) throws NullParameterException, InvalidItemException,
      InvalidQuantityException {
    if (item == null) {
      throw new NullParameterException();
    }
    if (quantity <= 0) {
      throw new InvalidQuantityException();
    }
    if (item.getName().length() > 64) {
      item.setName(item.getName().substring(0, 64));
    }
    try {
      ItemTypes.valueOf(item.getName().toUpperCase());
    } catch (Exception e) {
      throw new InvalidItemException();
    }
    
    if (items.containsKey(item)) {
      items.put(item, items.get(item) + quantity);
    }
    else {
      items.put(item, quantity);
    }
  }
  
  public void removeItem(Item item, int quantity) throws NullParameterException, ItemNotFoundException {
    if (item == null) {
      throw new NullParameterException();
    }
    else if (items.containsKey(item)) {
      int new_quantity = getQuantity(item) - quantity;
      
      if (new_quantity <= 0) {
        items.remove(item);
      } else {
        items.put(item, new_quantity);
      }
    }
    else {
      throw new ItemNotFoundException();
    }
  }
  
  public List<Item> getItems() {
    return new ArrayList<Item>(items.keySet());
  }
  
  public Customer getCustomer() {
    return this.customer;
  }
  
  public BigDecimal getTotal() {
    this.total = new BigDecimal(0);
    Iterator<Item> itemIterator = items.keySet().iterator();
    while (itemIterator.hasNext()) {
      this.total = this.total.add(new BigDecimal(items.get(itemIterator.next())));
    }
    return this.total;
  }
  
  public void clearCart() {
    this.items.clear();
  }
  
  public boolean checkOutCustomer(Context context) throws SQLException, DatabaseInsertException,
          UserNotFoundException, InvalidPriceException, NullParameterException,
          InvalidQuantityException, ItemNotFoundException, SaleNotFoundException {
    if (this.customer != null) {
      List<Item> cartItems = getItems();
      Item single_item;
      Inventory inventory = DatabaseSelectHelper.getInventory(context);
      for (int i = 0; i < cartItems.size(); i++) {
        single_item = cartItems.get(i);
        if (!inventory.getItemMap().containsKey(single_item)) {
          return false;
        } else if (inventory.getItemMap().get(single_item) < this.items.get(single_item)) {
          return false;
        }
      }
      
      for (int i = 0; i < cartItems.size(); i++) {
        single_item = cartItems.get(i);
        int quantity = inventory.getItemMap().get(single_item) - getQuantity(single_item);
        DatabaseUpdateHelper.updateInventoryQuantity(quantity, single_item.getId(), context);
        removeItem(single_item, quantity);
      }
      int saleId = DatabaseInsertHelper.insertSale(customer.getId(),
              getTotal().multiply(TAXRATE).setScale(2), context);
      
      for (int i = 0; i < cartItems.size(); i++) {
        DatabaseInsertHelper.insertItemizedSale(saleId, cartItems.get(i).getId(),
                this.items.get(cartItems.get(i)), context);
      }
      List<Integer> userAccounts = DatabaseSelectHelper.getUserAccounts(this.customer.getId(),
              context);
      for (int accountId : userAccounts) {
        DatabaseUpdateHelper.updateAccountStatus(accountId, false, context);
      }
      return true;
    }
    return false;
  }
  public int getQuantity(Item item) {
		return this.items.get(item);
	}
  public HashMap<Item, Integer> getInformation(){
	  return this.items;
  }
}

