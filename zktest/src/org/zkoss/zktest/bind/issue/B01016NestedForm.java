package org.zkoss.zktest.bind.issue;

import org.zkoss.bind.ValidationContext;
import org.zkoss.bind.Validator;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.Init;
import org.zkoss.bind.validator.AbstractValidator;
import org.zkoss.lang.Strings;

public class B01016NestedForm {

	String value1;
	Bean bean;
	
	@Init
	public void init(){
		value1 = "A";
		bean = new Bean();
		bean.value2 = "B";
	}
	
	public String getValue1() {
		return value1;
	}

	public void setValue1(String value1) {
		this.value1 = value1;
	}

	public Bean getBean() {
		return bean;
	}

	public void setBean(Bean bean) {
		this.bean = bean;
	}
	
	@Command
	public void update() {
		System.out.println("update "+getValue1()+","+getBean().getValue2());
	}

	public class Bean {
		String value2;

		public String getValue2() {
			return value2;
		}

		public void setValue2(String value2) {
			this.value2 = value2;
		}	
		
	}
	
	public Validator getValidator1(){
		return new AbstractValidator() {
			
			@Override
			public void validate(ValidationContext ctx) {
				String value = (String)ctx.getProperty().getValue();
				if(Strings.isEmpty(value)){
					addInvalidMessage(ctx, "cannot be empty");
				}else if(value.length()<3){
					addInvalidMessage(ctx, "length < 3");
				}
			}
		};
	}
}
