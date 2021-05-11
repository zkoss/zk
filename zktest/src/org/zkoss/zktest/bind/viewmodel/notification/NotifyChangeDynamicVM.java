/* NotifyChangeDynamicVM.java

	Purpose:
		
	Description:
		
	History:
		Wed May 05 15:02:45 CST 2021, Created by rudyhuang

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.bind.viewmodel.notification;

import org.zkoss.bind.BindUtils;
import org.zkoss.bind.annotation.BindingParam;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.Default;

/**
 * @author rudyhuang
 */
public class NotifyChangeDynamicVM {
	private int number;

	public int getNumber() {
		return number;
	}

	@Command
	public void add(@BindingParam @Default("true") boolean notify) {
		number += 5;
		if (notify) {
			BindUtils.postNotifyChange(this, "number");
		}
	}
}
