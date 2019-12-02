package com.b07.inventory;

import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;

public class InventoryImpl implements Inventory {
  private HashMap<Item, Integer> itemMap;
  
  public InventoryImpl() {
    this.itemMap = new HashMap<Item, Integer>();
  }
  
  @Override
  public List<Item> getItemList() {
    return new ArrayList<Item>(this.itemMap.keySet());
  }
  
  @Override
  public HashMap<Item, Integer> getItemMap() {
    return this.itemMap;
  }

  @Override
  public void setItemMap(HashMap<Item, Integer> itemMap) {
    if (itemMap == null) {
      this.itemMap = new HashMap<Item, Integer>();
    }
    this.itemMap = itemMap;
  }

  @Override
  public void updateMap(Item item, Integer value) {
    this.itemMap.put(item, value);
  }

  @Override
  public int getTotalItems() {
    int total = 0;
    for (int quantity : this.itemMap.values()) {
      total += quantity;
    }
    return total;
  }
}
