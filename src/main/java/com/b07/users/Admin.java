package com.b07.users;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.HashMap;

import com.b07.database.helper.DatabaseSelectHelper;
import com.b07.database.helper.DatabaseUpdateHelper;
import com.b07.exceptions.NullParameterException;
import com.b07.exceptions.UserNotFoundException;
import com.b07.inventory.Item;
import com.b07.store.Sale;
import com.b07.store.SalesLog;

public class Admin extends Employee implements Serializable{
	private static final long serialVersionUID = 4746028764517361203L;
	private transient boolean authenticated = false;

	public Admin(int id, String name, int age, String address) {
		super(id, name, age, address);
	}

	public Admin(int id, String name, int age, String address, boolean authenticated) {
		super(id, name, age, address, authenticated);
	}

	public boolean promoteEmployee(Employee employee) throws SQLException {
		int roleId = DatabaseSelectHelper.getUserRoleId(employee.getId());
		if (DatabaseSelectHelper.getRoleName(roleId).equalsIgnoreCase("EMPLOYEE")) {
			DatabaseUpdateHelper.updateUserRole(roleId, employee.getId());
			return true;
		}
		return false;
	}

	public void viewSalesHistory() throws SQLException, NullParameterException, UserNotFoundException {
		SalesLog salesLog = DatabaseSelectHelper.getSales();
		HashMap<Item, Integer> itemTally = new HashMap<Item, Integer>();
		BigDecimal sumPrice = new BigDecimal(0);

		for (Sale sale : salesLog.getSalesSet()) {
			System.out.println("Customer: " + sale.getUser().getName());
			System.out.println("Purchase Number: " + sale.getId());
			System.out.println("Total Purchase Price: " + sale.getTotalPrice().toString());
			sumPrice.add(sale.getTotalPrice());

			String indent = "Itemized Breakdown: ";
			for (Item item : sale.getItemMap().keySet()) {
				int quantity = sale.getItemMap().get(item);
				System.out.println(indent + item.getName() + ": " + quantity);
				indent = "                    ";
				if (itemTally.containsKey(item)) {
					itemTally.put(item, itemTally.get(item) + quantity);
				} else {
					itemTally.put(item, quantity);
				}
			}
			System.out.println("----------------------------------------------------------------");
		}

		for (Item item : itemTally.keySet()) {
			System.out.println("Number " + item.getName() + " Sold: " + itemTally.get(item));
		}
		System.out.println("TOTAL SALES: " + sumPrice.toString());
	}
}
