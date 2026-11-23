/* ListboxModelVM.java

	Purpose:
		
	Description:
		
	History:
		Created by Dennis

Copyright (C) 2011 Potix Corporation. All Rights Reserved.
 */

package org.zkoss.zktest.bind.issue;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.DependsOn;
import org.zkoss.bind.annotation.NotifyChange;
import org.zkoss.zul.ListModelList;

/**
 * @author Dennis Chen
 * 
 */
public class B00758Indirect {
	private List<Person> _persons;

	public B00758Indirect() {
		_persons = new ListModelList<Person>();
		for (int j = 0; j < 4; ++j) {
			_persons.add(new Person("First" + j, "Last" + j));
		}
	}

	public List<Person> getPersons() {
		return _persons;
	}

	@Command
	@NotifyChange("persons[0]")
	public void chang1stPerson() {
		_persons.set(0, new Person("Henri", "Chen"));
	}

	@Command
	@NotifyChange("persons[0].firstName")
	public void change1stPersonFirstName() {
		_persons.get(0).setFirstName("Tom");
	}

	public class Person {
		private String firstName;
		private String lastName;

		public Person(String firstName, String lastName) {
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

		@DependsOn({ "firstName", "lastName" })
		public String getFullName() {
			return firstName + " " + lastName;
		}
	}
}
