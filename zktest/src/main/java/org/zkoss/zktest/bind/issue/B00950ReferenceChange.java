package org.zkoss.zktest.bind.issue;

import java.util.ArrayList;
import java.util.List;

import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.DependsOn;
import org.zkoss.bind.annotation.NotifyChange;
import org.zkoss.zul.ListModel;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.ext.Selectable;

public class B00950ReferenceChange {
	private ListModelList<Person> personList;

	private Person selectedPerson;

	public B00950ReferenceChange() {
		personList = new ListModelList<Person>();
		personList.add(new Person("Dennis","Chen"));
		personList.add(new Person("Alice","Lin"));
		personList.add(null);
	}

	public List<Person> getPersonList() {
		return personList;
	}

	public Person getSelectedPerson() {
		return selectedPerson;
	}

	public void setSelectedPerson(Person selectedPerson) {
		this.selectedPerson = selectedPerson;
	}
	
	@Command @NotifyChange("selectedPerson")
	public void clearSelection() {
		selectedPerson = null;
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
		
		@DependsOn({"firstName","lastName"})
		public String getFullName(){
			return firstName+" "+lastName;
		}
	}
}
