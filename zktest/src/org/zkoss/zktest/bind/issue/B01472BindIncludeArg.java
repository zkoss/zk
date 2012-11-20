package org.zkoss.zktest.bind.issue;

import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.NotifyChange;

public class B01472BindIncludeArg {

	String staticValue = "ABC";
	int t=0;

	public String getStaticValue() {
		return staticValue;
	}

	public void setStaticValue(String staticValue) {
		this.staticValue = staticValue;
	}
	
	public String getZul(){
		return "B01472BindIncludeArgInner.zul?t="+t++;
	}
	
	@Command
	@NotifyChange("zul")
	public void reload(){
		
	}
}
