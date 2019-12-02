package com.b07.store;

import java.util.HashSet;

import com.b07.exceptions.NullParameterException;

public class SalesLogImpl implements SalesLog {
  private HashSet<Sale> salesSet;
  private int totalSales;
  
  public SalesLogImpl() {
    this.salesSet = new HashSet<Sale>();
    this.totalSales = 0;
  }
  
  public SalesLogImpl(HashSet<Sale> salesSet) throws NullParameterException {
    setSalesSet(salesSet);
  }
  
  @Override
  public HashSet<Sale> getSalesSet() {
    return this.salesSet;
  }

  @Override
  public void setSalesSet(HashSet<Sale> salesSet) throws NullParameterException {
    if (salesSet == null) {
      throw new NullParameterException();
    }
    this.salesSet = salesSet;
    this.totalSales = salesSet.size();
  }

  @Override
  public boolean addSale(Sale sale) throws NullParameterException {
    if (sale == null) {
      throw new NullParameterException();
    }
    if (salesSet.add(sale)) {
      this.totalSales++;
      return true;
    } else {
      return false;
    }
  }

  @Override
  public boolean removeSale(Sale sale) throws NullParameterException {
    if (sale == null) {
      throw new NullParameterException();
    }
    if (salesSet.remove(sale)) {
      this.totalSales--;
      return true;
    } else {
      return false;
    }
  }

  @Override
  public int getTotalSales() {
    return this.totalSales;
  }
}
