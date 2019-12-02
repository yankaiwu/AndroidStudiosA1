package com.b07.users;

import java.io.Serializable;

import com.b07.store.ShoppingCart;

public class AccountImpl implements Account, Serializable{
	private int id;
	private User user;
	private int userid;
	private ShoppingCart shoppingcart;
	private boolean active;
	private boolean approve;

	@Override
	public int getId() {
		// TODO Auto-generated method stub
		return this.id;
	}

	@Override
	public void setId(int id) {
		// TODO Auto-generated method stub
		this.id = id;
	}

	@Override
	public int getUserId() {
		// TODO Auto-generated method stub
		return this.userid;
	}

	@Override
	public void setUserId(int id) {
		// TODO Auto-generated method stub
		this.userid = id;
	}

	@Override
	public User getUser() {
		// TODO Auto-generated method stub
		return this.user;
	}


	@Override
	public ShoppingCart getShoppingCart() {
		// TODO Auto-generated method stub
		return this.shoppingcart;
	}

	@Override
	public void setShoppingCart(ShoppingCart shoppingcart) {
		// TODO Auto-generated method stub
		this.shoppingcart = shoppingcart;
	}

	@Override
	public void setActive(boolean active) {
		// TODO Auto-generated method stub
		this.active = active;
	}

	@Override
	public boolean getActive() {
		// TODO Auto-generated method stub
		return active;
	}

	@Override
	public void setApprove(boolean approve) {
		// TODO Auto-generated method stub
		this.approve = approve;
	}

	@Override
	public boolean getApprove() {
		// TODO Auto-generated method stub
		return approve;
	}

	@Override
	public void setUser(User user) {
		// TODO Auto-generated method stub
		
	}

}
