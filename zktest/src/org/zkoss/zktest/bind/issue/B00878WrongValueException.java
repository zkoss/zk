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

	public B00878WrongValueException() {
	}

	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}

	@Command
	public void save1(){
		
	}
	@Command
	public void save2(){
		
	}

	public Validator getValidator1(){
		return new AbstractValidator() {
			
			@Override
			public void validate(ValidationContext ctx) {
				String name = (String)ctx.getProperties("name")[0].getValue();
				if(!"Lin".equals(name)){
					addInvalidMessage(ctx, "the name have to equals to Lin, but is "+name);
				}
			}
		};
	}
}
