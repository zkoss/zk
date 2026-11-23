package org.zkoss.zktest.bind.issue;

import org.zkoss.bind.annotation.ExecutionArgParam;
import org.zkoss.bind.annotation.Init;
import org.zkoss.bind.annotation.ScopeParam;

public class B01469ScopeParamRefInner {

	String staticValue;
	
	@Init
	public void init(@ScopeParam("sValue") String staticValue){
		this.staticValue = staticValue;
	}

	public String getStaticValue() {
		return staticValue;
	}

	public void setStaticValue(String staticValue) {
		this.staticValue = staticValue;
	}
}
