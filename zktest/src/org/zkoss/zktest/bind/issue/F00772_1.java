package org.zkoss.zktest.bind.issue;

import org.zkoss.bind.annotation.Command;

public class F00772_1 {

	String value1;

	public String getValue1() {
		return value1;
	}

	public void setValue1(String value1) {
		this.value1 = value1;
	}
	
	@Command
	public void cmd1(){
		value1 = value1+"-local";
	}
	
}
