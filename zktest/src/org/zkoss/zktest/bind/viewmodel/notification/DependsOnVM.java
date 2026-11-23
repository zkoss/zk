/* NotifyChangeOnSetterVM.java

	Purpose:
		
	Description:
		
	History:
		Wed May 05 11:03:30 CST 2021, Created by rudyhuang

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.bind.viewmodel.notification;

import org.zkoss.bind.annotation.DependsOn;

/**
 * @author rudyhuang
 */
public class DependsOnVM {
	private String firstname = "John";
	private String lastname = "Smith";

	public String getFirstname() {
		return firstname;
	}

	public void setFirstname(String firstname) {
		this.firstname = firstname;
	}

	public String getLastname() {
		return lastname;
	}

	public void setLastname(String lastname) {
		this.lastname = lastname;
	}

	@DependsOn({"firstname", "lastname"})
	public String getFullname() {
		return String.format("%s %s", firstname, lastname);
	}
}