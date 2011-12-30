package org.zkoss.zktest.bind.basic;

import org.zkoss.bind.Binder;
import org.zkoss.bind.ValidationContext;
import org.zkoss.bind.Validator;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.NotifyChange;

public class LoadSavePromptCommandValidation {

	String value1;
	String value2;
	String value3;
	
	String msg1;
	String msg2;
	
	public LoadSavePromptCommandValidation(){
		value1 = "A";
		value2 = "B";
		value3 = "C";
	}

	public String getValue1() {
		return value1;
	}

	@NotifyChange
	public void setValue1(String value1) {
		this.value1 = value1;
	}

	public String getValue2() {
		return value2;
	}

	@NotifyChange
	public void setValue2(String value2) {
		this.value2 = value2;
	}

	public String getValue3() {
		return value3;
	}

	@NotifyChange
	public void setValue3(String value3) {
		this.value3 = value3;
	}
	
	
	
	
	public String getMsg1() {
		return msg1;
	}

	public String getMsg2() {
		return msg2;
	}

	public Validator getValidator1(){
		return new Validator() {
			public void validate(ValidationContext ctx) {
				Boolean v1 = (Boolean)ctx.getBindContext().getBindingArg("v1");
				Boolean v2 = (Boolean)ctx.getBindContext().getBindingArg("v2");
				String value = (String)ctx.getProperty().getValue();
				Binder binder = ctx.getBindContext().getBinder();
				if(Boolean.TRUE.equals(v1)){
					if("XX".equals(value) || "ZZ".equals(value)){
						msg1 = null;
					}else{
						msg1 = "value 1 has to be XX or ZZ";
						ctx.setInvalid();
					}
					binder.notifyChange(LoadSavePromptCommandValidation.this, "msg1");
				}else if(Boolean.TRUE.equals(v2)){
					if("YY".equals(value) || "ZZ".equals(value)){
						msg2 = null;
					}else{
						msg2 = "value 2 has to be YY or ZZ";
						ctx.setInvalid();
					}
					binder.notifyChange(LoadSavePromptCommandValidation.this, "msg2");
				}else{
					throw new RuntimeException("unknow target");
				}
			}
		};
	}
	
	@Command @NotifyChange("msg1")
	public void cmd1(){
		msg1 = "doCmd1";
	}
}
