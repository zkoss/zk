package org.zkoss.zktest.bind.issue;

import org.zkoss.bind.BindUtils;
import org.zkoss.bind.Form;
import org.zkoss.bind.annotation.BindingParam;
import org.zkoss.bind.annotation.Command;

public class B00911FormNotifyChange {
	private Person person;

	public Person getPerson() {
		return person;
	}

	public B00911FormNotifyChange() {
		person = new Person("Dennis");
	}
	
	public String getProp(){
		return "name";
	}
	
	@Command
	public void notify1(@BindingParam("fx") Form form) {
		form.setField("name", "Alex");
		BindUtils.postNotifyChange(null, null, form, "name");
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
