package org.zkoss.zktest.bind.issue;

import java.util.HashMap;
import java.util.Map;

import org.zkoss.bind.BindUtils;
import org.zkoss.bind.annotation.BindingParam;
import org.zkoss.bind.annotation.GlobalCommand;
//import org.zkoss.bind.annotation.CommandParam;
import org.zkoss.bind.annotation.NotifyChange;

public class F00772_4 {

	String value2;

	public String getValue2() {
		return value2;
	}

	public void setValue2(String value2) {
		this.value2 = value2;
	}

	@GlobalCommand({"cmdX","cmdY"})
	public void cmd1(@BindingParam("data") String value1){
		value2 = value1 + "-XMy";
		BindUtils.postNotifyChange("myqueue",null,this,"value2");
	}	
}
