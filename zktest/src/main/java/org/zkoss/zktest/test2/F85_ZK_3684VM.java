/* F85_ZK_3684VM.java

	Purpose:

	Description:

	History:
		Mon Jun 12 11:35:07 CST 2017, Created by rudyhuang

Copyright (C) 2017 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.test2;

import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

/**
 * @author rudyhuang
 */
public class F85_ZK_3684VM {
	private Person person = new Person("Henry");

	public Person getPerson() {
		return person;
	}

	public void setPerson(Person person) {
		this.person = person;
	}

	public class Person {
		private String firstName;

		public Person(String firstName) {
			this.firstName = firstName;
		}

		@Size(min = 1, message = "FirstName cannot be empty.")
		@Pattern(regexp = "[A-Z]([a-z]+)?", message = "FirstName should be capitalized and only letters are accepted.")
		public String getFirstName() {
			return firstName;
		}

		public void setFirstName(String firstName) {
			this.firstName = firstName;
		}
	}
}
