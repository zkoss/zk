package org.zkoss.zktest.test2;

import org.zkoss.bind.ValidationContext;
import org.zkoss.bind.Validator;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.Init;
import org.zkoss.bind.annotation.NotifyChange;

public class BB0_ZK_3333VM {
	private static final String START_MSG = "original message";

	private String message = START_MSG;
	private String midMsg = "";
	private String finalMsg = "";

	private Validator myValidator;

	@Init
	public void init() {
		myValidator = new Validator() {
			public void validate(ValidationContext ctx) {

				BB0_ZK_3333VM form = (BB0_ZK_3333VM) ctx.getProperty().getValue();
				String output = "";
				output += "\n";
				output += "MESSAGE CHANGED IN FORM : ";
				output += !START_MSG.equals(form.getMessage());
				output += "\n";
				output += "\n";
				output += "ORIGINAL MSG : ";
				output += START_MSG;
				output += "\n";
				output += "BEAN MSG : ";
				output += message;
				output += "\n";
				output += "PROXY MSG : ";
				output += form.getMessage();
				output += "\n";
				midMsg = output;
			}
		};
	}

	@Command
	@NotifyChange({"midMsg", "finalMsg"})
	public void validate() {
		finalMsg = message;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public Validator getMyValidator() {
		return myValidator;
	}

	public String getMidMsg() {
		return midMsg;
	}

	public String getFinalMsg() {
		return finalMsg;
	}
}