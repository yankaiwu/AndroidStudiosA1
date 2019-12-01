package com.b07.store;

import java.sql.Connection;
import java.util.HashMap;
import java.util.List;
import java.io.BufferedReader;
import java.io.InputStreamReader;

import com.b07.database.helper.DatabaseInsertHelper;
import com.b07.database.helper.DatabaseSelectHelper;
import com.b07.database.helper.DatabaseUpdateHelper;
import com.b07.exceptions.CredentialsNotFoundException;
import com.b07.exceptions.InvalidAccountTypeException;
import com.b07.inventory.Inventory;
import com.b07.inventory.InventoryImpl;
import com.b07.inventory.Item;
import com.b07.security.PasswordHelpers;
import com.b07.users.Account;
import com.b07.users.Admin;
import com.b07.users.Customer;
import com.b07.users.Employee;
import com.b07.users.Roles;
import com.b07.users.User;

public class SalesApplication {
  /**
   * This is the main method to run your entire program! Follow the "Pulling it together" 
   * instructions to finish this off.
   * @param argv unused.
   */
  public static void main(String[] argv) {
    
    Connection connection = DatabaseDriverExtender.connectOrCreateDataBase();
    if (connection == null) {
      System.out.print("NOOO");
    }
    try {
      Inventory inventory = new InventoryImpl();
      if (argv.length > 0 && argv[0].equals("-1")) {
        DatabaseDriverExtender.initialize(connection);
        for (Roles role: Roles.values()) {
          DatabaseInsertHelper.insertRole(role.toString());
        }
        
        String name = "Henry Hill-Tout";
        int age = 26;
        String address = "2 Barbara Street";
        int userId = DatabaseInsertHelper.insertNewUser(name, age, address, "SourSkittle");
        int roleId = Roles.valueOf("ADMIN").ordinal() + 1;
        DatabaseInsertHelper.insertUserRole(userId, roleId);
        
        name = "John Smith";
        age = 22;
        address = "123 King Street";
        userId = DatabaseInsertHelper.insertNewUser(name, age, address, "HotPotato");
        roleId = Roles.valueOf("EMPLOYEE").ordinal() + 1;
        DatabaseInsertHelper.insertUserRole(userId, roleId);
      }
      
      else if (argv.length > 0 && argv[0].equals("1")) {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));
        String input = "";
        int userId;
        String password = "";
        String db_password = "";
        
        while(true) {
          System.out.println("1 - Admin Login");
          System.out.println("2 - Exit");
          System.out.println("Enter Selection: ");
          input = bufferedReader.readLine();
          if (input.equals("1")) {
            System.out.println("Enter your User ID: ");
            userId = Integer.valueOf(bufferedReader.readLine());
            System.out.println("Enter your password: ");
            password = bufferedReader.readLine();
            try {
              db_password = DatabaseSelectHelper.getPassword(userId);
            } catch (Exception e) {
              throw new CredentialsNotFoundException();
            }
            if (PasswordHelpers.comparePassword(db_password, password)) {
              String userRole = DatabaseSelectHelper.getRoleName(DatabaseSelectHelper.getUserRoleId(userId));
              if (!userRole.equalsIgnoreCase("ADMIN")) {
                throw new InvalidAccountTypeException();
              }
              User userDetails = DatabaseSelectHelper.getUserDetails(userId);
              Admin currentAccount = new Admin(userId, userDetails.getName(), userDetails.getAge(),
                  userDetails.getAddress(), true);
              while (true) {
                System.out.println("Choose from one of the following options: ");
                System.out.println("1. Promote Employee");
                System.out.println("2. Display active/inactive accounts of a customer");
                System.out.println("3. Exit");
                input = bufferedReader.readLine();
                if (input.equals("1")) {
                  System.out.println("Enter the employee's id: ");
                  Integer employeeId = Integer.valueOf(bufferedReader.readLine());
                  User employeeDetails = DatabaseSelectHelper.getUserDetails(employeeId);
                  Employee employeeAccount = new Employee(employeeId, employeeDetails.getName(),
                      userDetails.getAge(), userDetails.getAddress(), true);
                  currentAccount.promoteEmployee(employeeAccount);
                }
                else if (input.equals("2")) {
                  System.out.println("Please enter the customer id: ");
                  String customerIdString = bufferedReader.readLine();
                  int customerId = Integer.parseInt(customerIdString);
                  List<Account> listOfActiveAccount;
                  listOfActiveAccount = DatabaseSelectHelper.getUserActiveAccounts(customerId);
                  System.out.println("Here is the list of active accounts of a given customer: ");
                  for (Account account: listOfActiveAccount) {
                	  System.out.println(String.valueOf(account) + String.valueOf(account.getId()));
                  }
                  
                  List<Account> listOfInactiveAccount;
                  listOfInactiveAccount = DatabaseSelectHelper.getUserInactiveAccounts(customerId);
                  System.out.println("------------------------");
                  System.out.println("Here is the list of inactive accounts of a given customer: ");
                  for (Account account: listOfInactiveAccount) {
                	  System.out.println(String.valueOf(account) + String.valueOf(account.getId()));
                  }
                }
                else if (input.equals("3")) {
                	break;
                }
              }
              break;
            }
            else {
              throw new CredentialsNotFoundException();
            }
          }
          else if (input.equals("2")) {
            break;
          }
        }
      }
      
      else {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));
        String input = "";
        int userId;
        String password = "";
        String db_password = "";
        Customer currentCustomer = null;
        Item item = null;
        int quantity;
        String name = "";
        int age = 0;
        String address = "";
        
        while(true) {
          System.out.println("1 - Employee Login");
          System.out.println("2 - Customer Login");
          System.out.println("0 - Exit");
          System.out.println("Enter Selection: ");
          input = bufferedReader.readLine();
          
          if (input.equals("1")) {
            System.out.println("Enter your User ID: ");
            userId = Integer.valueOf(bufferedReader.readLine());
            System.out.println("Enter your password: ");
            password = bufferedReader.readLine();
            try {
              db_password = DatabaseSelectHelper.getPassword(userId);
            } catch (Exception e) {
              throw new CredentialsNotFoundException();
            }

            if (PasswordHelpers.comparePassword(db_password, password)) {
              String userRole = DatabaseSelectHelper.getRoleName(DatabaseSelectHelper.getUserRoleId(userId));
              if (!(userRole.equalsIgnoreCase("EMPLOYEE") || userRole.equalsIgnoreCase("ADMIN"))) {
                throw new InvalidAccountTypeException();
              }
              User userDetails = DatabaseSelectHelper.getUserDetails(userId);
              Employee currentAccount = new Employee(userId, userDetails.getName(), userDetails.getAge(),
                  userDetails.getAddress(), true);
              EmployeeInterface currentInterface = new EmployeeInterface(currentAccount, inventory);
              while (true) {
                System.out.println("Choose from one of the following options: ");
                System.out.println("1. Authenticate new employee");
                System.out.println("2. Make new User");
                System.out.println("3. Make new Account");
                System.out.println("4. Make new Employee");
                System.out.println("5. Restock Inventory");
                System.out.println("6. Exit");
                input = bufferedReader.readLine();
                if (input.equals("1")) {
                  System.out.println("Enter your User ID: ");
                  userId = Integer.valueOf(bufferedReader.readLine());
                  System.out.println("Enter your password: ");
                  password = bufferedReader.readLine();
                  try {
                    db_password = DatabaseSelectHelper.getPassword(userId);
                  } catch (Exception e) {
                    throw new CredentialsNotFoundException();
                  } if (PasswordHelpers.comparePassword(db_password, password)) {
                    userRole = DatabaseSelectHelper.getRoleName(DatabaseSelectHelper.getUserRoleId(userId));
                    if (!userRole.equalsIgnoreCase("EMPLOYEE") || userRole.equalsIgnoreCase("ADMIN")) {
                      throw new InvalidAccountTypeException();
                    }
                  }
                  userDetails = DatabaseSelectHelper.getUserDetails(userId);
                  currentAccount = new Employee(userId, userDetails.getName(), userDetails.getAge(),
                      userDetails.getAddress(), true);
                  currentInterface.setCurrentEmployee(currentAccount);
                } else if (input.equals("2")) {
                  // TO DO!
                  // What does this mean???
                } else if (input.equals("3")) {
                  System.out.println("Please enter details about the new customer");
                  System.out.println("Name: ");
                  name = bufferedReader.readLine();
                  System.out.println("Age: ");
                  age = Integer.valueOf(bufferedReader.readLine());
                  System.out.println("Address: ");
                  address = bufferedReader.readLine();
                  System.out.println("Password: ");
                  password = bufferedReader.readLine();
                  System.out.println("The customer's user ID is: " + currentInterface.createCustomer(name, age,
                      address, password));
                } else if (input.equals("4")) {
                  System.out.println("Please enter details about the new employee");
                  System.out.println("Name: ");
                  name = bufferedReader.readLine();
                  System.out.println("Age: ");
                  age = Integer.valueOf(bufferedReader.readLine());
                  System.out.println("Address: ");
                  address = bufferedReader.readLine();
                  System.out.println("Password: ");
                  password = bufferedReader.readLine();
                  System.out.println("The employee's user ID is: " + currentInterface.createEmployee(name, age,
                      address, password));
                } else if (input.equals("5")) {
                  // Need to add support for adding items!
                  System.out.println("Please enter the item you wish to update: ");
                  name = bufferedReader.readLine();
                  System.out.println("Enter the new quantity of the item: ");
                  quantity = Integer.valueOf(bufferedReader.readLine());
                  currentInterface.restockInventory(item, quantity);
                } else if (input.equals("6")) {
                  break;
                }
              }
              break;
            } else {
              throw new CredentialsNotFoundException();
            }
            
          }
          else if (input.equals("2")) {
            System.out.println("Choose an option:");
            System.out.println("1. Login to an existing account.");
            System.out.println("2. Create a new account");
            System.out.println("3. Exit");
            input = bufferedReader.readLine();
            if (input.equals("1")) {
              System.out.println("Enter your User ID: ");
              userId = Integer.valueOf(bufferedReader.readLine());
              User userDetails = DatabaseSelectHelper.getUserDetails(userId);
              currentCustomer = new Customer(userId, userDetails.getName(), userDetails.getAge(),
                  userDetails.getAddress(), true);
            }
            else if (input.equals("2")) {
              name = bufferedReader.readLine();
              age = Integer.valueOf(bufferedReader.readLine());
              address = bufferedReader.readLine();
              bufferedReader.readLine();
              userId = DatabaseInsertHelper.insertNewUser(name, age, address, "SourSkittle");
              currentCustomer = new Customer(userId, name, age, address, true);
            }
            else {
              break;
            }
            //***************************************************************************
            ShoppingCart currentCart = new ShoppingCart(currentCustomer);
			int accountId = DatabaseSelectHelper.getUserAccounts(currentCustomer.getId());
			System.out.println("You have successfully logged in. " 
					+ "Would you like to retore your shopping cart?");
			System.out.println("Y - Yes, I would like to restore");
			System.out.println("N - No, I would like to creat an empty cart");
			if (bufferedReader.readLine().toLowerCase().equals("y")) {
				HashMap<Item, Integer> items = DatabaseSelectHelper.getAccountDetails(accountId);
				for (Item itemInPreviousCart : items.keySet()) {
					currentCart.addItem(itemInPreviousCart, items.get(itemInPreviousCart));
				}
			}else if (bufferedReader.readLine().toLowerCase().equals("n")) {
				HashMap<Item, Integer> items = DatabaseSelectHelper.getAccountDetails(accountId);
				for (Item itemInPreviousCart : items.keySet()) {
					DatabaseUpdateHelper.removeStoredAccountItem(accountId, itemInPreviousCart.getId());
				}
				System.out.println("Okay, you now have an empty shopping cart :>");
			}
			//****************************************************************************
            while (true) {
              System.out.println("Welcome to your Shopping Cart!");
              System.out.println("Please choose from one of the following options: ");
              System.out.println("1. List current items in cart");
              System.out.println("2. Add a quantity of an item to the cart");
              System.out.println("3. Check total price of items in the cart");
              System.out.println("4. Remove a quantity of an item from the cart");
              System.out.println("5. Check out ");
              System.out.println("6. Exit");
              input = bufferedReader.readLine();
              if (input.equals("1")) {
                List<Item> items = currentCart.getItems();
                for (int i = 0; i < items.size(); i++) {
                  System.out.println(DatabaseSelectHelper.getInventoryQuantity(items.get(i).getId()) +
                      items.get(i).getName());
                }
              } else if (input.equals("2")) {
                System.out.println("Please enter the item you wish to add: ");
                name = bufferedReader.readLine();
                System.out.println("Please enter the quantity you wish to add: ");
                quantity = Integer.valueOf(bufferedReader.readLine());
                item = DatabaseSelectHelper.getItem(DatabaseSelectHelper.getItemId(name));
                currentCart.addItem(item, quantity);
              } else if (input.equals("3")) {
                System.out.println("Subtotal: " + currentCart.getTotal());
              } else if (input.equals("4")) {
            	  System.out.println("Enter ID of the item you want to remove: ");
  				int itemId = Integer.parseInt(bufferedReader.readLine());
  				Item removeItem = DatabaseSelectHelper.getItem(itemId);
  				if (item == null) {
  					System.out.println("item Id is invaid, please enter again");
  					continue;
  				}
  				
  				System.out.println("Enter the quantity you want to remove: ");
  				int removeQuantity = Integer.parseInt(bufferedReader.readLine());
  				
  				if (removeQuantity < 0) {
  					System.out.println("Quantity is invalid, please enter again");
  					continue;
  				}
  				
  				currentCart.removeItem(removeItem, removeQuantity);
              } else if (input.equals("5")) {
            	  currentCart.checkOutCustomer();
            	  
            //**************************************************************************************
              } else if (input.equals("6")) {
            	  System.out.println("Dear customer, would you like to save your shopping cart?");
            	  System.out.println("Please enter your command as following instructions");
            	  System.out.println("1. Yes, I would like to save it for my next log in.");
            	  System.out.println("2, No, I would like to not save my current shopping cart.");
            	  if (bufferedReader.readLine().equals("1")){
            		  //delete all items from database first, 
            		  //then add all item in shopping cart to database.
            		  HashMap<Item, Integer> itemsInDatabase = DatabaseSelectHelper.getAccountDetails(accountId);
            		  for (Item itemToBeDeleted : itemsInDatabase.keySet()) {
            			  int itemToBeDeletedId = (int) itemToBeDeleted.getId();
            			  DatabaseUpdateHelper.removeStoredAccountItem(accountId, itemToBeDeletedId);
            		  }
            		  
	            	  for (Item itemInCart : currentCart.getItems()) {
	  					int itemId = itemInCart.getId();
	  					int quantityInCart = currentCart.getQuantity(item);
	  					DatabaseInsertHelper.insertAccountLine(accountId, itemId, quantityInCart);
	  					System.out.println("You have successfully saved your cart.");
	  					break;
	  				}}
	              else if (bufferedReader.readLine().equals("2")) {
	            	  currentCart.clearCart();
	            	  break;
	            	}
	            	
	        //**************************************************************************************
	            
                
              }
            }
          }
         else if (input.equals("0")) {
        	 break;
        	 }
          }
        
      
      }
    } catch (NumberFormatException e) {
      System.out.println("This field should be a number.");
    } catch (CredentialsNotFoundException e) {
      System.out.println("Invalid User ID or password.");
    } catch (InvalidAccountTypeException e) {
      System.out.println("User account type is not valid for this operation.");
    } catch (Exception e) {
      System.out.println("Something went wrong :(");
      System.out.println(e);
    } finally {
      try {
        connection.close();
      } catch (Exception e) {
        System.out.println("Looks like it was closed already :)");
      }
    }
    
  }}

