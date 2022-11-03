package org.zkoss.clientbind.test.issue;

import org.zkoss.bind.annotation.ExecutionArgParam;
import org.zkoss.bind.annotation.Init;

public class B01472BindIncludeArgInner {

	String staticValue;
	
	@Init
	public void init(@ExecutionArgParam("sValue") String staticValue){
		this.staticValue = staticValue;
	}

	public String getStaticValue() {
		return staticValue;
	}

	public void setStaticValue(String staticValue) {
		this.staticValue = staticValue;
	}
}
