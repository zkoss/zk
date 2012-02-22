/* CollectionIndexComposer.java

	Purpose:
		
	Description:
		
	History:
		Created by Dennis

Copyright (C) 2011 Potix Corporation. All Rights Reserved.
 */

package org.zkoss.zktest.bind.issue;

import org.zkoss.bind.Property;
import org.zkoss.bind.ValidationContext;
import org.zkoss.bind.Validator;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.validator.AbstractValidator;


/**
 * @author Dennis Chen
 * 
 */
public class B00878WrongValueException {

	String name;
	int age;
	int score;

	public B00878WrongValueException() {
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
	
	

	public int getScore() {
		return score;
	}

	public void setScore(int score) {
		this.score = score;
	}

	@Command
	public void save(){
		
	}


	public Validator getValidator1(){
		return new AbstractValidator() {
			
			@Override
			public void validate(ValidationContext ctx) {
				String name = (String)ctx.getProperties("name")[0].getValue();
				Integer age = (Integer)ctx.getProperties("age")[0].getValue();
				if(!"Lin".equals(name)){
					addInvalidMessage(ctx, "name","the name have to equal to Lin, but is "+name);
				}
				if(age==null || age<18){
					addInvalidMessage(ctx, "age", "the age have to large than 18, but is "+age);
				}
			}
		};
	}
}
