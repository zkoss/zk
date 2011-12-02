/* ZKBindLoad1Composer.java

	Purpose:
		
	Description:
		
	History:
		Aug 2, 2011 1:01:07 PM, Created by Dennis Chen

Copyright (C) 2011 Potix Corporation. All Rights Reserved.
 */

package org.zkoss.zktest.bind.basic;

import org.zkoss.bind.BindComposer;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.NotifyChange;

/**
 * @author Dennis Chen
 * 
 */
public class PropertyComposer extends BindComposer {
	private String value1;
	private String value2;
	private String value3;
	private String value4;

	public PropertyComposer() {
		value1 = "A";
		value2 = "B";
		value3 = "C";
		value4 = "D";
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

	public String getValue3() {
		return value3;
	}

	@NotifyChange
	public void setValue3(String value3) {
		this.value3 = value3;
	}

	public String getValue4() {
		return value4;
	}

	public void setValue4(String value4) {
		this.value4 = value4;
	}

	@Command 
	public void cmd1(){
		
	}
	@Command 
	public void cmd2(){
		value2 += "-by-cmd2";
	}
	@Command 
	public void cmd3(){
		value3 += "-by-cmd3";
	}
	
	@Command @NotifyChange("value3")
	public void change3(){
		value3 += "-by-change3";
	}
	
	@Command 
	public void cmd4(){
		
	}
}
