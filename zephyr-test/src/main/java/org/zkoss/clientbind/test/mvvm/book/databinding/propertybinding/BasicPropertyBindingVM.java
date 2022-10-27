/* BasicPropertyBindingVM.java
	Purpose:

	Description:

	History:
		Thu May 06 16:43:47 CST 2021, Created by jameschu

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.clientbind.test.mvvm.book.databinding.propertybinding;

import java.util.HashMap;
import java.util.Map;

import org.zkoss.bind.annotation.Init;
import org.zkoss.clientbind.test.mvvm.book.databinding.bean.Person;

/**
 * @author jameschu
 */
public class BasicPropertyBindingVM {
	private boolean maximized = false;
	private String welcomeMessage = "welcome!";
	private Person person;
	private Map<String, String> myMapping = new HashMap<>();
	private Map<String, Person> peopleMapping = new HashMap<>();

	@Init
	public void init() {
		person = new Person();
		person.setFirstName("Dennis");
		person.setLastName("Watson");
		person.setAge(18);
		myMapping.put("myKey", "123");
		Person p = new Person();
		p.setFirstName("Dennis");
		p.setLastName("Watson");
		p.setAge(40);
		peopleMapping.put("admin", p);
	}

	public boolean isMaximized() {
		return maximized;
	}

	public void setMaximized(boolean maximized) {
		this.maximized = maximized;
	}

	public String getWelcomeMessage() {
		return welcomeMessage;
	}

	public Person getPerson() {
		return person;
	}

	public void setPerson(Person person) {
		this.person = person;
	}

	public Map<String, String> getMyMapping() {
		return myMapping;
	}

	public void setMyMapping(Map<String, String> myMapping) {
		this.myMapping = myMapping;
	}

	public Map<String, Person> getPeopleMapping() {
		return peopleMapping;
	}

	public void setPeopleMapping(Map<String, Person> peopleMapping) {
		this.peopleMapping = peopleMapping;
	}
}
