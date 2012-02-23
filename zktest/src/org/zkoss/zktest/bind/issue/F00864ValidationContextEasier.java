/* CollectionIndexComposer.java

	Purpose:
		
	Description:
		
	History:
		Created by Dennis

Copyright (C) 2011 Potix Corporation. All Rights Reserved.
 */

package org.zkoss.zktest.bind.issue;

import java.util.Map;

import org.zkoss.bind.Form;
import org.zkoss.bind.Property;
import org.zkoss.bind.ValidationContext;
import org.zkoss.bind.Validator;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.validator.AbstractValidator;
import org.zkoss.lang.Objects;


/**
 * @author Dennis Chen
 * 
 */
public class F00864ValidationContextEasier {

	Person person;

	public F00864ValidationContextEasier() {
		person = new Person();
	}
	
	public Validator getValidator1(){
		return new AbstractValidator() {
			
			@Override
			public void validate(ValidationContext ctx) {
				Form form = (Form)ctx.getProperty().getValue();
				Map<String,Property> beanProps = ctx.getProperties(ctx.getProperty().getBase());
				Map<String,Property> formProps = ctx.getProperties(ctx.getProperty().getValue());
				String name1 = (String)beanProps.get("name").getValue();
				Integer age1 = (Integer)beanProps.get("age").getValue();
				String name2 = (String)formProps.get("name").getValue();
				Integer age2 = (Integer)formProps.get("age").getValue();
				
				if(!beanProps.get("name").getBase().equals(person)){
					addInvalidMessage(ctx, "error : base of name is not person");
				}else if(!beanProps.get("age").getBase().equals(person)){
					addInvalidMessage(ctx, "error : base of age is not person");
				}else if(!formProps.get("name").getBase().equals(form)){
					addInvalidMessage(ctx, "error : base of name is not form");
				}else if(!formProps.get("age").getBase().equals(form)){
					addInvalidMessage(ctx, "error : base of age is not form");
				}else if(!Objects.equals(name1,name2)){
					addInvalidMessage(ctx, "error : name is not equal:"+name1+","+name2);
				}else if(!Objects.equals(age1,age2)){
					addInvalidMessage(ctx, "error : age is not equal:"+age1+","+age2);
				}else if(formProps.size()!=2){
					addInvalidMessage(ctx, "error : form props size is "+formProps.size());
				}else if(beanProps.size()!=3){//include the form(.)
					addInvalidMessage(ctx, "error : bean props size is "+beanProps.size());
				}
			}
		};
	}
	
	public Person getPerson() {
		return person;
	}

	public void setPerson(Person person) {
		this.person = person;
	}

	@Command({"save1"})
	public void save(){
	}

	static public class Person {
		String name;
		
		int age;

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
