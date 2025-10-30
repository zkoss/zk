package org.zkoss.zktest.bind.issue;

import org.zkoss.bind.ValidationContext;
import org.zkoss.bind.Validator;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.NotifyChange;
import org.zkoss.bind.validator.AbstractValidator;

public class F00771 {

	String value1;
	String value2;
	String value3;

	public String getValue1() {
		return value1;
	}

	public void setValue1(String value1) {
		this.value1 = value1;
	}
	
	
	
	
	public String getValue2() {
		return value2;
	}

	public void setValue2(String value2) {
		this.value2 = value2;
	}
	
	

	public String getValue3() {
		return value3;
	}

	public void setValue3(String value3) {
		this.value3 = value3;
	}

	public Validator getValidator1(){
		return new AbstractValidator() {
			
			public void validate(ValidationContext ctx) {
				String val = (String)ctx.getProperty().getValue();
				if(!"abc".equalsIgnoreCase(val)){
					addInvalidMessage(ctx, "value1 must equalsIgnoreCase to abc");
					addInvalidMessage(ctx, "key1", "value1 must equalsIgnoreCase to abc - by key");
				}
			}
		};
	}
	
	public Validator getValidator2(){
		return new AbstractValidator() {
			
			public void validate(ValidationContext ctx) {
				String val = (String)ctx.getProperty().getValue();
				if(!"def".equalsIgnoreCase(val)){
					addInvalidMessage(ctx, "value2 must equalsIgnoreCase to def");
					addInvalidMessage(ctx, "key1", "value2 must equalsIgnoreCase to def - by key");
				}
			}
		};
	}
	
	public Validator getValidator3(){
		return new AbstractValidator() {
			
			public void validate(ValidationContext ctx) {
				String val = (String)ctx.getProperty().getValue();
				if(!"xyz".equalsIgnoreCase(val)){
					addInvalidMessage(ctx, "value3 must equalsIgnoreCase to xyz");
					addInvalidMessage(ctx, "key1", "value3 must equalsIgnoreCase to xyz - by key");
				}
			}
		};
	}
	
	public Validator getFormValidator(){
		return new AbstractValidator() {
			
			public void validate(ValidationContext ctx) {
				String val = (String)ctx.getProperties("value1")[0].getValue();
				if(!"abc".equalsIgnoreCase(val)){
					addInvalidMessage(ctx, "value1 must equalsIgnoreCase to abc");
					addInvalidMessage(ctx, "fkey1", "value1 must equalsIgnoreCase to abc - by key");
				}
				val = (String)ctx.getProperties("value2")[0].getValue();
				if(!"def".equalsIgnoreCase(val)){
					addInvalidMessage(ctx, "value2 must equalsIgnoreCase to def");
					addInvalidMessage(ctx, "fkey2", "value2 must equalsIgnoreCase to def - by key");
				}
				val = (String)ctx.getProperties("value3")[0].getValue();
				if(!"xyz".equalsIgnoreCase(val)){
					addInvalidMessage(ctx, "value3 must equalsIgnoreCase to xyz");
					addInvalidMessage(ctx, "fkey3", "value3 must equalsIgnoreCase to xyz - by key");
				}
			}
		};
	}
	
	@Command @NotifyChange({"value1"})
	public void reload1(){
		
	}
	
	@Command @NotifyChange({"value2"})
	public void reload2(){
		
	}
	
	@Command @NotifyChange({"value1","value2","value3"})
	public void submit(){
		
	}
	
}
