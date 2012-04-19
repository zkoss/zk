package org.zkoss.zktest.bind.issue;

import java.util.Map;

import org.zkoss.bind.Property;
import org.zkoss.bind.ValidationContext;
import org.zkoss.bind.Validator;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.NotifyChange;
import org.zkoss.bind.validator.AbstractValidator;
import org.zkoss.lang.Strings;

public class B01062NullIntValue {
	Integer value1;
	int value2;
	
	String message1;
	String message2;
	
	
	
	

	public Integer getValue1() {
		return value1;
	}

	public void setValue1(Integer value1) {
		this.value1 = value1;
	}

	public int getValue2() {
		return value2;
	}

	public void setValue2(int value2) {
		this.value2 = value2;
	}

	

	public String getMessage1() {
		return message1;
	}

	public void setMessage1(String message1) {
		this.message1 = message1;
	}

	public String getMessage2() {
		return message2;
	}

	public void setMessage2(String message2) {
		this.message2 = message2;
	}

	@Command @NotifyChange("message1")
	public void save() {
		message1 = "value1 is "+value1+", value2 is "+value2;
	}

	/**
	 * @return validator for expressions (check if expression is not empty)
	 */
	public Validator getValidator() {
		return new AbstractValidator() {
			@Override
			public void validate(ValidationContext ctx) {
				Map<String, Property> beanProps = ctx.getProperties(ctx.getProperty().getBase());
				Integer value1 = (Integer)beanProps.get("value1").getValue();
				Integer value2 = (Integer)beanProps.get("value2").getValue();
				
				message2 = "value1 is "+value1+", value2 is "+value2;
				
				ctx.getBindContext().getBinder().notifyChange(B01062NullIntValue.this, "message2");
			}
		};
	}
}