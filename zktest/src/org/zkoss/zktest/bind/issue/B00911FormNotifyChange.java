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
		person = new Person("Dennis","A");
	}
	
	public String getProp1(){
		return "name";
	}
	public String getProp2(){
		return "type";
	}
	
	@Command
	public void notify1(@BindingParam("fx") Form form) {
		form.setField("name", "Alex");
		form.setField("type", "B");
		BindUtils.postNotifyChange(null, null, form, "name");//notify name only
	}
	
	public class Person {
		private String name;
		private String type;
		public Person(String name,String type) {
			this.name = name;
			this.type = type;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getType() {
			return type;
		}

		public void setType(String type) {
			this.type = type;
		}
		
		
	}
}
