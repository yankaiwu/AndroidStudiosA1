package com.b07.users;

import java.sql.SQLException;
import java.util.Objects;

import com.b07.database.helper.DatabaseSelectHelper;
import com.b07.security.PasswordHelpers;

public abstract class User{
  private transient int id;
  private transient String name;
  private transient int age;
  private transient String address;
  private transient int roleId;
  @SuppressWarnings("unused")
  private transient boolean authenticated = false;
  
  public int getId() {
    return this.id;
  }
  public void setId(int id) {
    this.id = id;
  }
  public String getName() {
    return this.name;
  }
  public void setName(String name) {
    this.name = name;
  }
  public int getAge() {
    return this.age;
  }
  public void setAge(int age) {
    this.age = age;
  }
  public String getAddress() {
    return this.address;
  }
  public void setAddress(String address) {
    this.address = address;
  }
  public int getRoleId() {
    return this.roleId;
  }
  public void setRoleId(int roleId){
	  this.roleId = roleId;
  }
  public final boolean authenticate(String password) throws SQLException {
    if (PasswordHelpers.comparePassword(DatabaseSelectHelper.getPassword(id), password)) {
      this.authenticated = true;
      return true;
    }
    return false;
  }
  @Override
  public boolean equals(Object o) {
    if (o == null) {
      return false;
    } else if (this == o) {
      return true;
    } else if ((o instanceof User) && ((User) o).getId() == this.id) {
      return true;
    } else {
      return false;
    }
  }
  @Override
  public int hashCode() {
    return Objects.hash(this.id);
  }
}