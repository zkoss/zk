package org.zkoss.zktest.bind.issue;

import java.util.Date;

public class B01464IncludeReload2 {
	
	String value;
	public Date getNow(){
		return new Date();
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}
}
