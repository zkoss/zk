package org.zkoss.zktest.bind.basic;

import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.NotifyChange;

public class FormDirty {

	static public class Person {
		String name;

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

	Person person = new Person("Dennis");
	String msg;

	public Person getPerson() {
		return person;
	}

	public String getMsg() {
		return msg;
	}

	@Command @NotifyChange("msg")
	public void show(){
		msg = "old-name "+person.name;
	}

	@Command @NotifyChange({"msg","person"})
	public void save(){
		msg = "saved "+person.name;
	}

}
