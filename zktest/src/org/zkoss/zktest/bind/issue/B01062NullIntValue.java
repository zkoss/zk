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
	Integer value;
	
	String message;
	
	public Integer getValue() {
		return value;
	}

	public void setValue(Integer value) {
		this.value = value;
	}
	
	

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	@Command @NotifyChange("message")
	public void save() {
		message = "save value is "+value;
	}

	/**
	 * @return validator for expressions (check if expression is not empty)
	 */
	public Validator getValidator() {
		return new AbstractValidator() {
			@Override
			public void validate(ValidationContext ctx) {
				Map<String, Property> beanProps = ctx.getProperties(ctx.getProperty().getBase());
				Integer value = (Integer)beanProps.get("value").getValue();
				System.out.println(">>>>"+value);
			}
		};
	}
}