package org.zkoss.zktest.bind.issue;

import org.zkoss.bind.annotation.Command;

public class B00905FormNotifyChange {
	private Person person;

	public Person getPerson() {
		return person;
	}

	public B00905FormNotifyChange() {
		person = new Person("Dennis");
	}
	
	public String getProp(){
		return "name";
	}
	
	@Command
	public void save() {
		System.out.println("Save " + person.getName());
	}
	
	public class Person {
		private String name;
		public Person(String name) {
			this.name = name;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}
	}
}
