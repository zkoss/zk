package org.zkoss.zktest.bind.issue;

import java.util.HashMap;

import org.zkoss.bind.Form;
import org.zkoss.bind.ValidationContext;
import org.zkoss.bind.Validator;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.Init;
import org.zkoss.bind.annotation.NotifyChange;
import org.zkoss.bind.validator.AbstractValidator;
import org.zkoss.lang.Strings;
import org.zkoss.zul.Label;

public class B01017NestedFormPath {

	Bean bean;
	String msg;
	
	@Init
	public void init(){
		bean = new Bean();
		bean.value1 = "A";
		bean.value2 = "B";
	}

	public Bean getBean() {
		return bean;
	}

	public void setBean(Bean bean) {
		this.bean = bean;
	}
	
	
	
	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	@Command @NotifyChange("msg")
	public void update() {
		msg = "update value1:"+getBean().getValue1()+",value2:"+getBean().getValue2();
	}
	
	public String getKey2(){
		return "value2";
	}

	public class Bean {
		String value1;
		String value2;

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

		

	}
	
	public Validator getValidator1(){
		return new AbstractValidator() {
			
			@Override
			public void validate(ValidationContext ctx) {
				Label lab = (Label)ctx.getValidatorArg("info");
				
				String value = (String)ctx.getProperty().getValue();
				Object base = ctx.getProperty().getBase();
				String prop = ctx.getProperty().getProperty();
				System.out.println(">>validate: base:"+base+",value:"+value+",prop:"+prop);
				
				if(!(base instanceof Form)){
					lab.setValue("base is not a 'Form', is '"+base.getClass()+"'");
				}else if(!"bean.value1".equals(prop)){
					lab.setValue("prop is not 'bean.value1', is '"+prop+"'");
				}
			}
		};
	}
	public Validator getValidator2(){
		return new AbstractValidator() {
			
			@Override
			public void validate(ValidationContext ctx) {
				Label lab = (Label)ctx.getValidatorArg("info");
				
				String value = (String)ctx.getProperty().getValue();
				Object base = ctx.getProperty().getBase();
				String prop = ctx.getProperty().getProperty();
				System.out.println(">>validate: base:"+base+",value:"+value+",prop:"+prop);
				
				if(!(base instanceof Form)){
					lab.setValue("base is not a 'Form', is '"+base.getClass()+"'");
				}else if(!"bean.value2".equals(prop)){
					lab.setValue("prop is not 'bean.value2', is '"+prop+"'");
				}
			}
		};
	}
}
