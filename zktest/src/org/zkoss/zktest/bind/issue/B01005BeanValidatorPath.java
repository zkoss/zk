package org.zkoss.zktest.bind.issue;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotEmpty;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.Init;
import org.zkoss.bind.annotation.NotifyChange;


public class B01005BeanValidatorPath {

	@Valid
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
		msg = "update value1:"+getBean().getValue1();
	}
	
	public String getKey2(){
		return "value2";
	}

	public class Bean {
		@NotEmpty
		@NotNull
		@Length(min=3,message = "min length is 3")
		String value1;

		public String getValue1() {
			return value1;
		}

		public void setValue1(String value1) {
			this.value1 = value1;
		}

	}
}
