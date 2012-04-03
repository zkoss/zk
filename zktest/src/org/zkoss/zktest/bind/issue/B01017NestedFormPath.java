package org.zkoss.zktest.bind.issue;

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
		msg = "update "+getBean().getValue1();
	}

	public class Bean {
		String value1;

		public String getValue1() {
			return value1;
		}

		public void setValue1(String value1) {
			this.value1 = value1;
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
				System.out.println(">>>>> base "+base+",value "+value+", prop:"+prop);
				
				if(!(base instanceof Form)){
					lab.setValue("base is not a 'Form', is '"+base.getClass()+"'");
				}else if(!"bean.value1".equals(prop)){
					lab.setValue("prop is not 'bean.value1', is '"+prop+"'");
				}
			}
		};
	}
}
