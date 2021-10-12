/* ForEachVersusChildrenBindingVM.java

		Purpose:
		
		Description:
		
		History:
				Wed May 05 16:27:26 CST 2021, Created by leon

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.bind.advance;

import java.util.ArrayList;
import java.util.List;

import org.zkoss.bind.annotation.Init;
import org.zkoss.bind.annotation.NotifyChange;
import org.zkoss.zk.ui.annotation.Command;

public class ForEachVersusChildrenBindingVM {
	List<Person> personList = new ArrayList<>();

	public List<Person> getPersonList() {
		return personList;
	}

	@Init
	public void init() {
		personList.add(new Person("AAA", "aaa"));
		personList.add(new Person("BBB", "bbb"));
		personList.add(new Person("CCC", "ccc"));
		personList.add(new Person("DDD", "ddd"));
	}

	@NotifyChange("personList")
	@Command
	public void addNewPerson() {
		personList.add(new Person("NEW", "new"));
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
	}
}
