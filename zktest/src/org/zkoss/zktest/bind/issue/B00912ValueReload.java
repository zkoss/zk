package org.zkoss.zktest.bind.issue;

import org.zkoss.bind.ValidationContext;
import org.zkoss.bind.Validator;
import org.zkoss.bind.validator.AbstractValidator;

public class B00912ValueReload {

	String value = "KGB";

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}
	
	public String getProp(){
		return "value";
	}
	
	public Validator getValidator(){
		return new AbstractValidator(){
			@Override
			public void validate(ValidationContext ctx) {
				if(!"def".equals(ctx.getProperty().getValue())){
					addInvalidMessage(ctx, "value has to be def");
				}
			}
		};
	}
}
