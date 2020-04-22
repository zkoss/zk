/* F91_ZK_4523VM.java

	Purpose:
		
	Description:
		
	History:
		Fri Mar 06 11:51:43 CST 2020, Created by rudyhuang

Copyright (C) 2020 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.test2;

import org.zkoss.bind.annotation.BindingParam;
import org.zkoss.bind.annotation.BindingParams;
import org.zkoss.bind.annotation.Command;
import org.zkoss.zk.ui.util.Clients;

/**
 * @author rudyhuang
 */
public class F91_ZK_4523VM {
	@Command
	public void greeting(@BindingParam("firstName") String firstName,
	                     @BindingParam("lastName") String lastName,
	                     @BindingParam("age") int age) {
		Clients.log(String.format("Greetings, %s %s!", firstName, lastName));
	}

	@Command("greeting-bean")
	public void greetingBean(@BindingParams POJO person) {
		Clients.log(String.format("Greetings, %s %s!", person.getFirstName(), person.getLastName()));
	}

	public static class POJO {
		private String _firstName;
		private String _lastName;
		private int _age;

		public String getFirstName() {
			return _firstName;
		}

		public void setFirstName(String firstName) {
			this._firstName = firstName;
		}

		public String getLastName() {
			return _lastName;
		}

		public void setLastName(String lastName) {
			this._lastName = lastName;
		}

		public int getAge() {
			return _age;
		}

		public void setAge(int age) {
			this._age = age;
		}
	}
}
