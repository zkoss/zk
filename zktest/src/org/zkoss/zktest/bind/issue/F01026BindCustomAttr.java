package org.zkoss.zktest.bind.issue;

import org.zkoss.bind.annotation.BindingParam;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.NotifyChange;
import org.zkoss.zk.ui.Component;

public class F01026BindCustomAttr {

	String value1 = "A";
	String value2 = "B";
	String value3 = "C";
	
	String message;
	
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

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	@Command @NotifyChange({"message","value1","value2","value3"})
	public void test1(@BindingParam("comp") Component comp){
		String val1 = (String)comp.getAttribute("value1");
		String val2 = (String)comp.getAttribute("value2");
		String val3 = (String)comp.getAttribute("value3");
		message = "value1:"+val1+",value2:"+val2+",value3:"+val3;
		value1 = "x";
		value2 = "y";
		value3 = "z";
	}
}
