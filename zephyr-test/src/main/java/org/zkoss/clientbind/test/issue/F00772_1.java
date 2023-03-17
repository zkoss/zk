package org.zkoss.clientbind.test.issue;

import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.NotifyChange;

public class F00772_1 {

	String value1;

	public String getValue1() {
		return value1;
	}

	public void setValue1(String value1) {
		this.value1 = value1;
	}
	
	@Command
	@NotifyChange("value")
	public void cmd1(){
		value1 = value1+"-local";
	}
	
}
