package com.b07.inventory;

import java.sql.SQLException;
import java.util.HashMap;

import com.b07.database.helper.DatabaseInsertHelper;
import com.b07.database.helper.DatabaseSelectHelper;
import com.b07.database.helper.DatabaseUpdateHelper;
import com.b07.exceptions.ItemNotFoundException;

public class InventoryImpl implements Inventory {
  private HashMap<Item, Integer> itemMap;
  private int total;
  
  public InventoryImpl() throws SQLException, ItemNotFoundException {
    // Need to figure out whether my implementation or Kevin's is what we want.
    this.itemMap = DatabaseSelectHelper.getInventoryHashMap();
  }
  
  @Override
  public HashMap<Item, Integer> getItemMap() {
    return this.itemMap;
  }

  @Override
  public void setItemMap(HashMap<Item, Integer> itemMap) {
    // This method shouldn't be used because there's no way to remove items from the database.
  }

  @Override
  public void updateMap(Item item, Integer value) {
    int itemId;
    try {
      itemId = DatabaseSelectHelper.getItemId(item.getName());
      if (itemMap.containsKey(item)) {
        DatabaseUpdateHelper.updateInventoryQuantity(value, itemId);
      } else {
        DatabaseInsertHelper.insertInventory(itemId, value);
      }
      itemMap.put(item, value);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  @Override
  public int getTotalItems() {
    return this.total;
  }

  @Override
  public void setTotalItems(int total) {
    this.total = total;
  }

}
