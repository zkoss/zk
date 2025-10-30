package org.zkoss.zktest.bind.basic;

import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.ExecutionArgParam;
import org.zkoss.bind.annotation.ExecutionParam;
import org.zkoss.bind.annotation.Init;
import org.zkoss.bind.annotation.NotifyChange;

public class ExecutionParamVM {

	String param1;
	String arg1;
	
	@Init
	public void init(@ExecutionParam("param1") String param1,@ExecutionArgParam("arg1") String arg1){
		this.param1 = param1;
		this.arg1 = arg1;
	}

	public String getParam1() {
		return param1;
	}

	public void setParam1(String param1) {
		this.param1 = param1;
	}

	public String getArg1() {
		return arg1;
	}

	public void setArg1(String arg1) {
		this.arg1 = arg1;
	}

	@NotifyChange("*") @Command
	public void cmd1(@ExecutionParam("param1") String param1,@ExecutionArgParam("arg1") String arg1){
		this.param1 = param1;
		this.arg1 = arg1;
	}
	
	
}
