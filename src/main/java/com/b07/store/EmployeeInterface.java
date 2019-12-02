package com.b07.store;

import android.content.Context;

import java.sql.SQLException;
import java.util.HashSet;

import com.b07.database.helper.DatabaseInsertHelper;
import com.b07.database.helper.DatabaseSelectHelper;
import com.b07.exceptions.DatabaseInsertException;
import com.b07.exceptions.DuplicateUserLoginException;
import com.b07.exceptions.InvalidRoleException;
import com.b07.exceptions.InvalidUserLoginException;
import com.b07.exceptions.NullParameterException;
import com.b07.exceptions.UserNotFoundException;
import com.b07.inventory.Inventory;
import com.b07.inventory.Item;
import com.b07.users.Employee;
import com.b07.users.Roles;
import com.b07.users.User;

public class EmployeeInterface {
  private Employee currentEmployee = null;
  private Inventory inventory;
  
  public EmployeeInterface(Employee employee, Inventory inventory) {
    // Give message if employee is not authenticated?
    this.inventory = inventory;
    if (employee != null && employee.isAuthenticated()) {
      this.currentEmployee = employee;
    }
  }
  
  public EmployeeInterface(Inventory inventory) {
    this.inventory = inventory;
  }
  
  public void setCurrentEmployee(Employee employee) {
    // Give message if employee is not authenticated?
    if (employee != null && employee.isAuthenticated()) {
      this.currentEmployee = employee;
    }
  }
  
  public boolean hasCurrentEmployee() {
    if (this.currentEmployee == null) {
      return false;
    }
    return true;
  }
  
  public boolean restockInventory(Item item, int quantity) {
    inventory.updateMap(item, quantity);
    return true;
  }
  
  public int createCustomer(String name, int age, String address, String password, String login,
                            Context context)
      throws DatabaseInsertException, NullParameterException, SQLException,
      UserNotFoundException, InvalidUserLoginException, DuplicateUserLoginException {
    int userId = DatabaseInsertHelper.insertNewUser(name, age, address, password, login, context);
    DatabaseInsertHelper.insertUserRole(userId, Roles.valueOf("CUSTOMER").ordinal() + 1,
            context);
    return userId;
  }
  
  public int createEmployee(String name, int age, String address, String password, String login,
                            Context context)
      throws DatabaseInsertException, NullParameterException, SQLException,
      UserNotFoundException, InvalidUserLoginException, DuplicateUserLoginException {
    int userId = DatabaseInsertHelper.insertNewUser(name, age, address, password, login, context);
    DatabaseInsertHelper.insertUserRole(userId, Roles.valueOf("EMPLOYEE").ordinal() + 1,
        context);
    return userId;
  }
  
  public HashSet<User> searchUserLogin(String query, Context context) throws UserNotFoundException,
      NullParameterException, InvalidRoleException {
    HashSet<User> loginList = DatabaseSelectHelper.getUserLoginsByQuery(query, context);
    return loginList;
  }
}
