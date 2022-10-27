/* PerformanceVM.java
	Purpose:

	Description:

	History:
		Mon Mar 14 14:22:14 CST 2022, Created by jameschu

Copyright (C) 2022 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.clientbind.benchmark;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.zkoss.bind.annotation.Init;

/**
 * @author jameschu
 */
public class PerformanceVM {
	private String welcomeMessage = "welcome!";
	private Person person;
	private List<Person> persons;
	private Map<String, String> myMapping = new HashMap<>();

	@Init
	public void init() {
		person = new Person();
		person.setFirstName("Dennis");
		person.setLastName("Watson");
		person.setAge(18);
		myMapping.put("myKey", "123");

		persons = new ArrayList<>();
		for (int i = 0; i < 10; i++) {
			Person p = new Person();
			p.setFirstName("Dennis " + i);
			p.setLastName("Watson " + i);
			p.setAge(18);
			persons.add(p);
		}
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

	public List<Person> getPersons() {
		return persons;
	}
}
