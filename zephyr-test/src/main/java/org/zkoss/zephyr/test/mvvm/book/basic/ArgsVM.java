package org.zkoss.zephyr.test.mvvm.book.basic;

import org.zkoss.bind.BindComposer;
import org.zkoss.bind.BindContext;
import org.zkoss.bind.BindUtils;
import org.zkoss.bind.Converter;
import org.zkoss.bind.ValidationContext;
import org.zkoss.bind.Validator;
import org.zkoss.bind.annotation.BindingParam;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.NotifyChange;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.util.Clients;

/**
 * @author jameschu
 */
public class ArgsVM {
	private String value1;
	private String value2;

	private String message3;
	private String message4;


	public ArgsVM() {
		value1 = "A";
		value2 = "B";
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

	public String getMessage3() {
		return message3;
	}

	public void setMessage3(String message3) {
		this.message3 = message3;
	}

	public String getMessage4() {
		return message4;
	}

	public void setMessage4(String message4) {
		this.message4 = message4;
	}

	public String getMyArg1() {
		return "myarg1";
	}

	public String getMyArg2() {
		return "myarg2";
	}

	public String getParam2() {
		return "Chen";
	}

	public Converter getConverter1() {
		return new Converter() {
			public Object coerceToUi(Object val, Component component, BindContext ctx) {
				return val + "-" + ctx.getConverterArg("arg1");
			}

			public Object coerceToBean(Object val, Component component, BindContext ctx) {
				return val + "-" + ctx.getConverterArg("arg2");
			}
		};
	}

	public Validator getValidator1() {
		return new Validator() {
			public void validate(ValidationContext ctx) {
				String text = (String) ctx.getBindContext().getValidatorArg("text");
				String value = (String) ctx.getProperty().getValue();
				if (!value.equals(text)) {
					ctx.setInvalid();
					setMessage3("value have to equals " + text);
				} else {
					setMessage3(null);
				}
				BindUtils.postNotifyChange(ArgsVM.this, "message3");
			}

		};
	}

	public Validator getValidator2() {
		return new Validator() {
			public void validate(ValidationContext ctx) {
				String text = (String) ctx.getBindContext().getValidatorArg("text");
				String value = (String) ctx.getProperties("value1")[0].getValue();
				if (!value.equals(text)) {
					ctx.setInvalid();
					setMessage4("value have to equals " + text);
				} else {
					setMessage4(null);
				}
				BindUtils.postNotifyChange(ArgsVM.this, "message4");
			}
		};
	}

	@NotifyChange("*")
	@Command
	public void cmd1(@BindingParam("param1") String param1, @BindingParam("param2") String param2) {
		this.value1 += param1;
		this.value2 += param2;
	}

	@NotifyChange(".")
	@Command
	public void cmd2() {
		setMessage4("execute cmd2");
	}

	@Command
	public void cmd3() {
		Clients.log("value2: " + value2);
	}
}
