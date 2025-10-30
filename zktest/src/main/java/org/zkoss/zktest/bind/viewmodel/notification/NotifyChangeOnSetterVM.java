/* NotifyChangeOnSetterVM.java

	Purpose:
		
	Description:
		
	History:
		Wed May 05 11:03:30 CST 2021, Created by rudyhuang

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.bind.viewmodel.notification;

import org.zkoss.bind.annotation.NotifyChange;

/**
 * @author rudyhuang
 */
public class NotifyChangeOnSetterVM {
	private String firstname = "John";
	private String lastname = "Smith";

	public String getFirstname() {
		return firstname;
	}

	@NotifyChange("fullname") // no firstname notification
	public void setFirstname(String firstname) {
		this.firstname = firstname;
	}

	public String getLastname() {
		return lastname;
	}

	@NotifyChange({"lastname", "fullname"})
	public void setLastname(String lastname) {
		this.lastname = lastname;
	}

	public String getFullname() {
		return String.format("%s %s", firstname, lastname);
	}
}