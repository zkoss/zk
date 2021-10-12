package org.zkoss.zktest.test2;

import org.zkoss.bind.ValidationContext;
import org.zkoss.bind.Validator;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.Init;
import org.zkoss.bind.validator.AbstractValidator;
import org.zkoss.zul.ListModelList;

public class B80_ZK_3233VM {

	private String name;

	private Validator validator;

	@Init
	public void init() {
		validator = new AbstractValidator() {

			@Override
			public void validate(ValidationContext ctx) {
				String name = (String) ctx.getProperty().getValue();
				if (name != null) {
					addInvalidMessage(ctx, "name", "Mandatory field!");
				}
			}
		};
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	@Command
	public void validate() {
		System.out.println("Validation Passed");
	}

	public Validator getValidator() {
		return validator;
	}
}
