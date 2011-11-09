/* ZKBindLoad1Composer.java

	Purpose:
		
	Description:
		
	History:
		Aug 2, 2011 1:01:07 PM, Created by henri

Copyright (C) 2011 Potix Corporation. All Rights Reserved.
 */

package org.zkoss.zktest.zbind.basic;

import org.zkoss.bind.BindComposer;
import org.zkoss.bind.NotifyChange;

/**
 * @author Dennis Chen
 * 
 */
public class CommandIndirectComposer extends BindComposer {
	private String value1;
	private boolean checked;

	public CommandIndirectComposer() {
		value1 = "no-command";
		checked = true;
	}

	public String getValue1() {
		return value1;
	}

	@NotifyChange
	public void setValue1(String value1) {
		this.value1 = value1;
	}

	public boolean isChecked() {
		return checked;
	}

	@NotifyChange
	public void setChecked(boolean checked) {
		System.out.println(">>>>>>>>>>>set checked "+checked);
		this.checked = checked;
	}

	@NotifyChange("value1")
	public void command1(){
		//notifychange only work when method call by binder (by el&binder), so, call this in vm doesn't notify change,
		//you should add notify change of this command method too
		setValue1("by command1");
	}
	
	@NotifyChange("value1")
	public void command2(){
		setValue1("by command2");
	}
}
