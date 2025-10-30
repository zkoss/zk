package org.zkoss.zktest.bind.issue;

import org.zkoss.bind.annotation.ExecutionArgParam;
import org.zkoss.bind.annotation.Init;
import org.zkoss.bind.annotation.ScopeParam;

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
