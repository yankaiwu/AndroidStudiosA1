package com.b07.inventory;

import java.util.HashMap;
import java.util.List;

public interface Inventory {
  public List<Item> getItemList();
  public HashMap<Item, Integer> getItemMap();
  public void setItemMap(HashMap<Item, Integer> itemMap);
  public void updateMap(Item item, Integer value);
  public int getTotalItems();
}
