package org.zkoss.zktest.bind.issue;

import org.zkoss.bind.BindContext;
import org.zkoss.bind.Converter;
import org.zkoss.bind.Form;
import org.zkoss.bind.FormStatus;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.NotifyChange;
import org.zkoss.zk.ui.Component;

public class B01088FormUpdate {
	Person person = new Person("Dennis","Chen");
	

	public Person getPerson() {
		return person;
	}

	public void setPerson(Person person) {
		this.person = person;
	}

	@Command @NotifyChange("person")
	public void reload(){
		person = new Person("Dennis","Chen");
	}
	
	@Command @NotifyChange("person")
	public void save(){
	}
	
	
	public static class Person{
		String firstName;
		String lastName;
		public Person() {}
		public Person(String firstName,String lastName){
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
		
	}
	
	public Converter<Object, Person, Component> getConverter1(){
		return new Converter<Object, Person, Component>() {

			
			public Object coerceToUi(Person val, Component component, BindContext ctx) {
				return val.getFirstName()+" "+ val.getLastName();
			}

			
			public Person coerceToBean(Object val, Component component, BindContext ctx) {
				return null;
			}
		};
	}
	public Converter<Object, FormStatus, Component> getConverter2(){
		return new Converter<Object, FormStatus, Component>() {

			
			public Object coerceToUi(FormStatus val, Component component, BindContext ctx) {
				return val.isDirty();
			}

			
			public FormStatus coerceToBean(Object val, Component component, BindContext ctx) {
				return null;
			}
		};
	}
}