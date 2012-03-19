package org.zkoss.zktest.bind.issue;

import java.util.ArrayList;
import java.util.List;

import org.zkoss.bind.annotation.DependsOn;

public class B00950ReferenceChange {
	private List<Person> personList;

	private Person selectedPerson;

	public B00950ReferenceChange() {
		personList = new ArrayList<Person>();
		personList.add(new Person("Dennis","Chen"));
		personList.add(new Person("Alice","Lin"));
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
