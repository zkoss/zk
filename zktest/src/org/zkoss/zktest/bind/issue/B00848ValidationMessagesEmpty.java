package org.zkoss.zktest.bind.issue;

import org.zkoss.bind.Form;
import org.zkoss.bind.Property;
import org.zkoss.bind.ValidationContext;
import org.zkoss.bind.Validator;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.NotifyChange;
import org.zkoss.bind.validator.AbstractValidator;

public class B00848ValidationMessagesEmpty {

	String value1 = "ABC";
	Integer value2 = 10;
	public String getValue1() {
		return value1;
	}
	public void setValue1(String value1) {
		this.value1 = value1;
	}
	public Integer getValue2() {
		return value2;
	}
	public void setValue2(Integer value2) {
		this.value2 = value2;
	}
	
	public Validator getValidator1(){
		return new AbstractValidator() {
			public void validate(ValidationContext ctx) {
				String val = (String)ctx.getProperty().getValue();
				if(!"abc".equalsIgnoreCase(val)){
					addInvalidMessage(ctx, "value must equals ignore case 'abc', but is "+val);
				}
			}
		};
	}
	
	public Validator getValidator2(){
		return new AbstractValidator() {
			public void validate(ValidationContext ctx) {
				Integer val = (Integer)ctx.getProperty().getValue();
				if(val==null || val<10 || val>100){
					addInvalidMessage(ctx, "value must not < 10 or > 100, but is "+val);
				}
			}
		};
	}
	
	public Validator getValidator3(){
		return new AbstractValidator() {
			public void validate(ValidationContext ctx) {
				String val1 = (String)((Form)ctx.getProperty().getValue()).getField("value1");
				
				if(!"AbC".equals(val1)){
					addInvalidMessages(ctx, new String[]{"value must equals 'AbC', but is "+val1,"extra validation info "+val1});
				}
			}
		};
	}
	
	
	@Command
	public void cmd1(){
		
	}
	
	@Command
	public void cmd2(){
		
	}
	
	@Command @NotifyChange({"value1"})
	public void cmd3(){
		
	}
	
}
