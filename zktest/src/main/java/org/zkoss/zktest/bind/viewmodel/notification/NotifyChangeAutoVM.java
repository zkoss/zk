/* NotifyChangeAutoVM.java

	Purpose:
		
	Description:
		
	History:
		Thu May 06 11:29:42 CST 2021, Created by rudyhuang

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.bind.viewmodel.notification;

import java.security.SecureRandom;

import org.zkoss.bind.annotation.AutoNotifyChange;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.NotifyChange;
import org.zkoss.bind.annotation.SmartNotifyChange;

/**
 * @author rudyhuang
 */
@AutoNotifyChange
public class NotifyChangeAutoVM {
	private String firstname = "John";
	private String lastname = "Smith";

	public String getFirstname() {
		return firstname;
	}

	@SmartNotifyChange("fullname") // notice: SmartNotifyChange doesn't override @NotifyChange
	public void setFirstname(String firstname) {
		this.firstname = firstname;
	}

	public String getLastname() {
		return lastname;
	}

	@NotifyChange("lastname")
	@SmartNotifyChange("fullname") // @NotifyChange + @SmartNotifyChange
	public void setLastname(String lastname) {
		this.lastname = lastname;
	}

	public String getFullname() {
		return String.format("%s %s", firstname, lastname);
	}

	@Command
	public void randomize() {
		setFirstname(generateRandomString());
		setLastname(generateRandomString());
	}

	private String generateRandomString() {
		return new SecureRandom().ints(10, 'A', 'z' + 1)
				.filter(Character::isAlphabetic)
				.collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
				.toString();
	}
}
