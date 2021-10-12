package org.zkoss.zktest.bind.issue;

import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.NotifyChange;
import org.zkoss.bind.annotation.NotifyChangeDisabled;
import org.zkoss.bind.annotation.BindingParam;

public class F00687 {

	String value1 = "A";
	String value2 = "B";
	String value3 = "C";
	String value4 = "D";

	public F00687() {
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

	@NotifyChangeDisabled
	public void setValue2(String value2) {
		this.value2 = value2;
	}

	public String getValue3() {
		return value3;
	}

	@NotifyChange({ "value2", "value3" })
	public void setValue3(String value3) {
		this.value3 = value3;
	}

	public String getValue4() {
		return value4;
	}

	public void setValue4(String value4) {
		this.value4 = value4;
	}

	@Command @NotifyChange("value4")
	public void cmd1(@BindingParam("val") String val) {
		value4 = val;
	}

}
