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
public class F0013 extends BindComposer {
	private String value1;
	private String value2;


	public F0013() {
		value1 = "A";
		value2 = "B";
	}

	public String getValue1() {
		return value1;
	}

	@NotifyChange
	public void setValue1(String value1) {
		this.value1 = value1;
	}

	public String getValue2() {
		return value2;
	}

	@NotifyChange
	public void setValue2(String value2) {
		this.value2 = value2;
	}

	//notify property, but not base object
	@Command @NotifyChange("*")
	public void cmd1(){
		this.value1 += "-cmd1";
		this.value2 += "-cmd1";
	}
	
	//notify base object, include all it property
	@Command @NotifyChange(".")
	public void cmd2(){
		this.value1 += "-cmd2";
		this.value2 += "-cmd2";
	}
	
	@Command 
	public void cmd3(){
		this.value1 += "-cmd3";
		this.value2 += "-cmd3";
		//notify base object, include all it property
		notifyChange(this, ".");
	}	
}
