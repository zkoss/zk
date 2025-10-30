/* NotifyChangeDisabledVM.java

	Purpose:
		
	Description:
		
	History:
		Wed May 05 16:25:59 CST 2021, Created by rudyhuang

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.bind.viewmodel.notification;

import org.zkoss.bind.annotation.NotifyChange;
import org.zkoss.bind.annotation.NotifyChangeDisabled;
import org.zkoss.bind.annotation.SmartNotifyChange;

/**
 * @author rudyhuang
 */
public class NotifyChangeDisabledVM {
	private String val0 = "test";
	private String val1 = "test";
	private String val2 = "test";
	private String val3 = "test";

	public String getVal0() {
		return val0;
	}

	public void setVal0(String val0) {
		this.val0 = val0;
	}

	public String getVal1() {
		return val1;
	}

	@NotifyChangeDisabled
	public void setVal1(String val1) {
		this.val1 = val1;
	}

	public String getVal2() {
		return val2;
	}

	@NotifyChange("*")
	@NotifyChangeDisabled
	public void setVal2(String val2) {
		this.val2 = val2;
	}

	public String getVal3() {
		return val3;
	}

	@SmartNotifyChange("*")
	@NotifyChangeDisabled
	public void setVal3(String val3) {
		this.val3 = val3;
	}
}
