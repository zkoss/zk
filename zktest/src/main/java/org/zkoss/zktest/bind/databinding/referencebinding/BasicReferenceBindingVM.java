/* BasicReferenceBindingVM.java
	Purpose:

	Description:

	History:
		Fri May 07 11:03:59 CST 2021, Created by jameschu

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.bind.databinding.referencebinding;

import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.Init;
import org.zkoss.bind.annotation.NotifyChange;
import org.zkoss.zktest.bind.databinding.bean.Person;

/**
 * @author jameschu
 */
public class BasicReferenceBindingVM {
	private Person person;
	private int num;

	@Init
	public void init() {
		person = new Person();
		person.setFirstName("Dennis");
		person.setLastName("Watson");
	}

	public Person getPerson() {
		return person;
	}

	public void setPerson(Person person) {
		this.person = person;
	}

	@Command
	@NotifyChange("person")
	public void append() {
		person.setFirstName(person.getFirstName() + 1);
	}

	@Command
	@NotifyChange("person")
	public void update() {
		person = new Person();
		person.setFirstName("Mary");
		person.setLastName("King");
	}

	public int getNum() {
		return num;
	}

	public void setNum(int num) {
		this.num = num;
	}
}
