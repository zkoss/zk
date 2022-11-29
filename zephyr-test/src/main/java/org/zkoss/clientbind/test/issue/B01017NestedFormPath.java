package org.zkoss.clientbind.test.issue;

import org.zkoss.bind.BindUtils;
import org.zkoss.bind.Form;
import org.zkoss.bind.ValidationContext;
import org.zkoss.bind.Validator;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.Init;
import org.zkoss.bind.annotation.NotifyChange;
import org.zkoss.bind.validator.AbstractValidator;

public class B01017NestedFormPath {

	Bean bean;
	String msg;

	@Init
	public void init() {
		bean = new Bean();
		bean.value1 = "A";
		bean.value2 = "B";
		bean.value3 = "C";
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

	@Command
	@NotifyChange("msg")
	public void update() {
		msg = "update value1:" + getBean().getValue1() + ",value2:" + getBean().getValue2() + ",value3:" + getBean().getValue3();
		;
	}

	public String getKey3() {
		return "value3";
	}

	public String getInfo1() {
		return info1;
	}

	public void setInfo1(String info1) {
		this.info1 = info1;
	}

	public String getInfo2() {
		return info2;
	}

	public void setInfo2(String info2) {
		this.info2 = info2;
	}

	public String getInfo3() {
		return info3;
	}

	public void setInfo3(String info3) {
		this.info3 = info3;
	}

	public static class Bean {
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


	}

	private String info1 = "";
	private String info2 = "";
	private String info3 = "";

	public Validator getValidator1() {
		return new AbstractValidator() {


			public void validate(ValidationContext ctx) {
				String value = (String) ctx.getProperty().getValue();
				Object base = ctx.getProperty().getBase();
				String prop = ctx.getProperty().getProperty();
//				System.out.println(">>validate: base:"+base+",value:"+value+",prop:"+prop);
				String info = "";
				if (!(base instanceof Form)) {
					info = "base is not a 'Form', is '" + base.getClass() + "'";
				} else if (!"bean.value1".equals(prop)) {
					info = "prop is not 'bean.value1', is '" + prop + "'";
				} else {
					info = "value is '" + value + "'";
				}
				setInfo1(info);
				BindUtils.postNotifyChange(B01017NestedFormPath.this, "info1");
			}
		};
	}

	public Validator getValidator2() {
		return new AbstractValidator() {


			public void validate(ValidationContext ctx) {
				String value = (String) ctx.getProperty().getValue();
				Object base = ctx.getProperty().getBase();
				String prop = ctx.getProperty().getProperty();
//				System.out.println(">>validate: base:"+base+",value:"+value+",prop:"+prop);
				String info = "";
				if (!(base instanceof Form)) {
					info = "base is not a 'Form', is '" + base.getClass() + "'";
				} else if (!"bean['value2']".equals(prop)) {
					info = "prop is not 'bean['value2']', is '" + prop + "'";
				} else {
					info = "value is '" + value + "'";
				}
				setInfo2(info);
				BindUtils.postNotifyChange(B01017NestedFormPath.this, "info2");
			}
		};
	}

	public Validator getValidator3() {
		return new AbstractValidator() {


			public void validate(ValidationContext ctx) {
				String value = (String) ctx.getProperty().getValue();
				Object base = ctx.getProperty().getBase();
				String prop = ctx.getProperty().getProperty();
//				System.out.println(">>validate: base:"+base+",value:"+value+",prop:"+prop);
				String info = "";
				if (!(base instanceof Form)) {
					info = "base is not a 'Form', is '" + base.getClass() + "'";
				} else if (!"bean[vm.key3]".equals(prop)) {
					info = "prop is not 'bean[vm.key3]', is '" + prop + "'";
				} else {
					info = "value is '" + value + "'";
				}
				setInfo3(info);
				BindUtils.postNotifyChange(B01017NestedFormPath.this, "info3");
			}
		};
	}
}