package org.zkoss.clientbind.test.book.basic;

import org.zkoss.bind.Property;
import org.zkoss.bind.Validator;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.NotifyChange;
import org.zkoss.lang.Strings;
import org.zkoss.zk.ui.util.Clients;

/**
 * @author jameschu
 */
public class ValidationVM {
	private int value1;
	private String value2;
	private Validator validator1 = ctx -> {
		if (!ctx.isValid()) return;
		Property p = ctx.getProperty();
		Object val = p.getValue();
		if (val != null && !Strings.isBlank(val.toString()) && Integer.parseInt(val.toString()) > 10) {
			setLastMessage1(null);
		} else {
			ctx.setInvalid();
			setLastMessage1("value 1 have to large than 10");
		}
		ctx.getBindContext().getBinder().notifyChange(ValidationVM.this, "lastMessage1");
		Clients.log(getLastMessage1());
	};

	private Validator validator2 = ctx -> {
		if (!ctx.isValid()) return;
		Property p = ctx.getProperty();
		Object val = p.getValue();
		if (val != null && !Strings.isBlank(val.toString()) && Integer.parseInt(val.toString()) > 20) {
			setLastMessage2(null);
		} else {
			ctx.setInvalid();
			setLastMessage2("value 2 have to large than 20");
		}
		ctx.getBindContext().getBinder().notifyChange(ValidationVM.this, "lastMessage2");
		Clients.log(getLastMessage2());
	};

	public int getValue1() {
		return value1;
	}

	@NotifyChange
	public void setValue1(int value1) {
		this.value1 = value1;
	}

	public String getValue2() {
		return value2;
	}

	@NotifyChange
	public void setValue2(String value2) {
		this.value2 = value2;
	}

	private String lastMessage1;
	private String lastMessage2;

	public String getLastMessage1() {
		return lastMessage1;
	}

	public void setLastMessage1(String lastMessage1) {
		this.lastMessage1 = lastMessage1;
	}

	public String getLastMessage2() {
		return lastMessage2;
	}

	public void setLastMessage2(String lastMessage2) {
		this.lastMessage2 = lastMessage2;
	}

	@Command
	public void cmd1() {
		Clients.log("cmd1 is called");
	}

	@Command
	public void cmd2() {
		Clients.log("cmd2 is called");
	}

	public Validator getValidator1() {
		return validator1;
	}

	public Validator getValidator2() {
		return validator2;
	}
}