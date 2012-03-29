package org.zkoss.zktest.bind.issue;

import org.zkoss.bind.annotation.BindingParam;
import org.zkoss.bind.annotation.Init;

public class B00994InitParam {

	
	String value;
	
	@Init
	public void init(@BindingParam("val") String value){
		this.value = value;
	}

	public String getValue() {
		return value;
	}
}