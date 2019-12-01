package com.b07.users;

import java.io.Serializable;

public class Employee extends User implements Serializable{
	private static final long serialVersionUID = -2898005930000773405L;
	private transient boolean authenticated = false;

	public Employee(int id, String name, int age, String address) {
		super.setId(id);
		super.setName(name);
		super.setAge(age);
		super.setAddress(address);
	}

	public Employee(int id, String name, int age, String address, boolean authenticated) {
		super.setId(id);
		super.setName(name);
		super.setAge(age);
		super.setAddress(address);
		this.authenticated = true;
	}

	public boolean isAuthenticated() {
		return this.authenticated;
	}
}
