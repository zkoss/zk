package org.zkoss.zktest.bind.issue;

import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.ContextParam;
import org.zkoss.bind.annotation.ContextType;
import org.zkoss.bind.annotation.DefaultCommand;
import org.zkoss.bind.annotation.DefaultGlobalCommand;
import org.zkoss.bind.annotation.GlobalCommand;
import org.zkoss.bind.annotation.NotifyChange;


public class F01416DefaultCommand {
	
	String value="Dennis";
	
	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	@Command
	@NotifyChange("value")
	public void cmd1(){
		value = "do command1";
		
	}
	
	@Command("cmd3")
	@NotifyChange("value")
	public void cmd3(){
		value = "do command3";
	}
	
	@DefaultCommand
	@NotifyChange("value")
	public void cmd(@ContextParam(ContextType.COMMAND_NAME) String cmdName){
		value = "do command "+cmdName;
	}
	
	@GlobalCommand
	@NotifyChange("value")
	public void gcmd1(){
		value = "do globa-command1";
		
	}
	
	@GlobalCommand("gcmd3")
	@NotifyChange("value")
	public void gcmd3(){
		value = "do globa-command3";
	}
	
	@DefaultGlobalCommand
	@NotifyChange("value")
	public void gcmd(@ContextParam(ContextType.COMMAND_NAME) String cmdName){
		value = "do globa-command "+cmdName;
	}
}
