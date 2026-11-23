package org.zkoss.zktest.bind.issue;

import org.zkoss.bind.annotation.BindingParam;
import org.zkoss.bind.annotation.GlobalCommand;
//import org.zkoss.bind.annotation.CommandParam;
import org.zkoss.bind.annotation.NotifyChange;

public class F00772_2 {

	String value2;

	public String getValue2() {
		return value2;
	}

	public void setValue2(String value2) {
		this.value2 = value2;
	}

	@GlobalCommand({"cmdX","cmdE"}) @NotifyChange("value2")
	public void cmd1(@BindingParam("data") String value1){
		value2 = value1 + "-X1";
	}
}
