package com.b07.store;

import java.util.HashSet;

import com.b07.exceptions.NullParameterException;

public interface SalesLog {
  
  public HashSet<Sale> getSalesSet();
  
  public void setSalesSet(HashSet<Sale> salesSet) throws NullParameterException;
  
  public boolean addSale(Sale sale) throws NullParameterException;
  
  public boolean removeSale(Sale sale) throws NullParameterException;
  
  public int getTotalSales();
  
}