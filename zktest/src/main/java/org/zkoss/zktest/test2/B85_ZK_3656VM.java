/* B85_ZK_3656VM.java

	Purpose:
		
	Description:
		
	History:
		Thu Jan 04 18:25:09 CST 2018, Created by rudyhuang

Copyright (C) 2018 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.test2;

import java.util.List;

import org.zkoss.bind.annotation.Init;
import org.zkoss.zul.AbstractListModel;
import org.zkoss.zul.ListModelList;

/**
 * @author rudyhuang
 */
public class B85_ZK_3656VM {
	private ListModelList<Person> persons;

	private List<Person> selectedPersons;

	@Init
	public void init() {
		this.persons = new ListModelList<Person>();
		this.persons.setMultiple(true);
		this.persons.setSelectionControl(new AbstractListModel.DefaultSelectionControl<Person>(this.persons) {
			@Override
			public boolean isSelectable(final Person person) {
				return person.isCheckable();
			}
		});
		for (int i = 0; i < 100; i++)
			this.persons.add(new Person("" + i, "" + i, i == 20 || i == 44));
	}

	public ListModelList<Person> getPersons() {
		return persons;
	}

	public List<Person> getSelectedPersons() {
		return selectedPersons;
	}

	public void setSelectedPersons(List<Person> selectedPersons) {
		this.selectedPersons = selectedPersons;
	}

	public class Person {
		private String firstName;

		private String lastName;

		private boolean checkable;

		public Person(String firstName, String lastName, boolean checkable) {
			this.firstName = firstName;
			this.lastName = lastName;
			this.checkable = checkable;
		}

		public String getFirstName() {
			return firstName;
		}

		public String getLastName() {
			return lastName;
		}

		public boolean isCheckable() {
			return checkable;
		}
	}
}
