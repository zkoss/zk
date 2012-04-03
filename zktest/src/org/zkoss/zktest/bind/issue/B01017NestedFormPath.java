package org.zkoss.zktest.bind.issue;

import org.zkoss.bind.Form;
import org.zkoss.bind.ValidationContext;
import org.zkoss.bind.Validator;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.Init;
import org.zkoss.bind.validator.AbstractValidator;
import org.zkoss.lang.Strings;

public class B01017NestedFormPath {

	Bean bean;
	
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
	
	@Command
	public void update() {
		System.out.println("update "+getBean().getValue1());
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
				String value = (String)ctx.getProperty().getValue();
				Object base = ctx.getProperty().getBase();
				String prop = ctx.getProperty().getProperty();
				System.out.println(">>>>> base "+base+",value "+value+", prop:"+prop);
				if(!(base instanceof Form)){
					addInvalidMessage(ctx, "base is not a Form");
				}else if(!"bean.value1".equals(prop)){
					addInvalidMessage(ctx, "prop is not bean.value1");
				}else if(Strings.isEmpty(value)){
					addInvalidMessage(ctx, "cannot be empty");
				}else if(value.length()<3){
					addInvalidMessage(ctx, "length < 3");
				}
				
				
				
			}
		};
	}
}
