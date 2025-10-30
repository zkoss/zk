/* 

	Purpose:
		
	Description:
		
	History:
		Aug 2, 2011 1:01:07 PM, Created by henri

Copyright (C) 2011 Potix Corporation. All Rights Reserved.
 */

package org.zkoss.zktest.bind.issue;

import org.zkoss.bind.BindComposer;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.NotifyChange;

/**
 * @author Dennis Chen
 * 
 */
public class F0015 extends BindComposer {
	private String value1;
	private String value2;
	private String value3;


	public F0015() {
		value1 = "A";
		value2 = "B";
		value3 = "C";
	}

	public String getValue1() {
		return value1;
	}

	public String getValue2() {
		return value2;
	}
	
	public String getValue3() {
		return value3;
	}


	//notify property, but not base object
	@Command @NotifyChange("*")
	public void cmd1(){
		this.value1 = "doCommand1";
	}
	
	@Command @NotifyChange("*")
	public void cmd2(){
		this.value2 = "doCommand2";
		getBinder().postCommand("cmd3", null);
	}
	
	@Command @NotifyChange("*")
	public void cmd3(){
		this.value3 = "doCommand3";
	}
	
	
}
