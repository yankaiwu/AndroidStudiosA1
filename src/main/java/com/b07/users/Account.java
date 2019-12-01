package com.b07.users;

import com.b07.store.ShoppingCart;

public interface Account {
	public int getId();
	public void setId(int id);
	public int getUserId();
	public void setUserId(int id);
	public User getUser();
	public void setUser(User user);
	public ShoppingCart getShoppingCart();
	public void setShoppingCart(ShoppingCart shoppingcart);
	public void setActive(boolean active);
	public boolean getActive();
	public void setApprove(boolean approve);
	public boolean getApprove();
	

}
