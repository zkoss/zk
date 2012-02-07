package org.zkoss.zktest.bind.issue;

import org.zkoss.bind.annotation.Init;
import org.zkoss.bind.annotation.NotifyChange;

public class F00638Base {

	String value1;
	
	String value3;
	
	public String getValue1() {
		return value1;
	}

	public void setValue1(String value1) {
		this.value1 = value1;
	}	

	public String getValue3() {
		return value3;
	}

	public void setValue3(String value3) {
		this.value3 = value3;
	}

	@Init
	public void init(){
		this.value1 = "X1";
		this.value3 = "Y1";
	}
}
