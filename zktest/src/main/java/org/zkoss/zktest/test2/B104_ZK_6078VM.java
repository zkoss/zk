/* B104_ZK_6078VM.java

        Purpose:
                
        Description:
                
        History:
                Wed Mar 25 16:22:23 CST 2026, Created by peakerlee

Copyright (C) 2026 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.test2;

import java.util.ArrayList;
import java.util.List;

import org.zkoss.bind.BindUtils;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.NotifyChange;

public class B104_ZK_6078VM {

	private final List<Employee> employeeList = new ArrayList<>();
	private Employee selectedEmployee;

	public B104_ZK_6078VM() {
		employeeList.add(new Employee("Max", "Mustermann"));
		employeeList.add(new Employee("Erika", "Musterfrau"));
		employeeList.add(new Employee("John", "Miller"));
	}

	public List<Employee> getEmployeeList() {
		return employeeList;
	}

	public Employee getSelectedEmployee() {
		return selectedEmployee;
	}

	public void setSelectedEmployee(Employee selectedEmployee) {
		this.selectedEmployee = selectedEmployee;
	}

	@Command
	@NotifyChange({"selectedEmployee", "employeeList"})
	public void save() {
	}

	@Command
	public void cancel() {
		BindUtils.postNotifyChange(null, null, this, "selectedEmployee");
		BindUtils.postNotifyChange(null, null, this, "employeeList");
	}

	public static class Employee {
		private String firstName;
		private String lastName;

		public Employee() {
		}

		public Employee(String firstName, String lastName) {
			this.firstName = firstName;
			this.lastName = lastName;
		}

		public String getFirstName() {
			return firstName;
		}

		public void setFirstName(String firstName) {
			this.firstName = firstName;
		}

		public String getLastName() {
			return lastName;
		}

		public void setLastName(String lastName) {
			this.lastName = lastName;
		}
	}
}
