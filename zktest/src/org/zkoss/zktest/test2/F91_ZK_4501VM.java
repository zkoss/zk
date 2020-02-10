/* F91_ZK_4501VM.java

		Purpose:
		
		Description:
		
		History:
				Fri Feb 07 17:14:32 CST 2020, Created by leon

Copyright (C) 2020 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.test2;

import org.zkoss.bind.BindUtils;
import org.zkoss.bind.FormLegacy;
import org.zkoss.bind.SimpleForm;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.Init;
import org.zkoss.zk.ui.util.Clients;

public class F91_ZK_4501VM {
	User user;
	FormLegacy myForm;
	
	@Init
	public void init() {
		user = new User("guest", 20);
		myForm = new SimpleForm();
	}
	
	@Command
	public void test() {
		Clients.log("value in simple form: " + myForm.getField("name") + ", " + myForm.getField("age"));
		Clients.log("value in original object: " + user.getName() + ", " + user.getAge());
	}
	
	@Command
	public void setDataInForm() {
		myForm.setField("name", "Peter");
		myForm.setField("age", 12);
		BindUtils.postNotifyChange(null, null, myForm, "name", "age");
	}
	
	@Command
	public void save() {
	}
	
	public FormLegacy getMyForm() {
		return myForm;
	}
	
	public void setMyForm(FormLegacy myForm) {
		this.myForm = myForm;
	}
	
	public User getUser() {
		return user;
	}
	
	public void setUser(User user) {
		this.user = user;
	}
	
	public class User {
		String name;
		int age;
		
		public User(String name, int age) {
			this.name = name;
			this.age = age;
		}
		
		public String getName() {
			return name;
		}
		
		public void setName(String name) {
			this.name = name;
		}
		
		public int getAge() {
			return age;
		}
		
		public void setAge(int age) {
			this.age = age;
		}
	}
}
