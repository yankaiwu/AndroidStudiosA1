package com.b07.store;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Objects;

import com.b07.inventory.Item;
import com.b07.users.User;

public class SaleImpl implements Sale {
  private int id;
  private User user;
  private BigDecimal price;
  private HashMap<Item, Integer> itemMap;
  
  public SaleImpl() {
    this.id = 0;
    this.user = null;
    this.price = null;
    this.itemMap = null;
  }
  
  @Override
  public int getId() {
    return this.id;
  }

  @Override
  public void setId(int id) {
    this.id = id;
  }

  @Override
  public User getUser() {
    return this.user;
  }

  @Override
  public void setUser(User user) {
    this.user = user;
  }

  @Override
  public BigDecimal getTotalPrice() {
    return this.price;
  }

  @Override
  public void setTotalPrice(BigDecimal price) {
    this.price = price;
  }

  @Override
  public HashMap<Item, Integer> getItemMap() {
    return this.itemMap;
  }

  @Override
  public void setItemMap(HashMap<Item, Integer> itemMap) {
    this.itemMap = itemMap;
  }
  
  @Override
  public boolean equals(Object o) {
    if (o == null) {
      return false;
    } else if (o == this) {
      return true;
    } else if ((o instanceof Sale) && ((Sale) o).getId() == getId()) {
      return true;
    } else {
      return false;
    }
  }
  
  @Override
  public int hashCode() {
    return Objects.hash(getId());
  }
}
