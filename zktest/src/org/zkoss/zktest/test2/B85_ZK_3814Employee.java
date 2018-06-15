/* B85_ZK_3814Employee.java

	Purpose:
		
	Description:
		
	History:
		Thu Jun 07 12:06:25 CST 2018, Created by rudyhuang

Copyright (C) 2018 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.test2;

/**
 * @author rudyhuang
 */
public class B85_ZK_3814Employee implements B85_ZK_3814Interface {
	private String firstName;
	private String lastName;

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	@Override
	public String getFirstName() {
		return firstName;
	}

	@Override
	public String getLastName() {
		return lastName;
	}
}
