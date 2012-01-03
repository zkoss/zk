package org.zkoss.zktest.bind.issue;

import org.zkoss.bind.ValidationContext;
import org.zkoss.bind.Validator;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.NotifyChange;
import org.zkoss.bind.validator.AbstractValidator;

public class B00722 {

	String value1 = "abc";
	
	public Validator getValidator(){
		return new AbstractValidator() {
			
			public void validate(ValidationContext ctx) {
				String value = (String)ctx.getProperty().getValue();
				if(!"abc".equals(value) && !"ABC".equals(value)){
					addInvalidMessage(ctx,"the value has to be 'abc' or 'ABC'");
				}
			}
		};
	}
	
	
	
	public String getValue1() {
		return value1;
	}



	public void setValue1(String value1) {
		this.value1 = value1;
	}



	@Command @NotifyChange("value1")
	public void save(){
		value1 = value1+":saved";
	}
	
	@Command @NotifyChange(".")
	public void reload(){
	}
}
