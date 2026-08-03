/* F104_ZK_6097_ConfirmpopupVM.java

	Purpose:

	Description:

	History:
		Wed May 13 13:05:00 CST 2026, Created by peakerlee

Copyright (C) 2026 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.test2;

import org.zkoss.bind.annotation.BindingParam;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.Init;
import org.zkoss.bind.annotation.NotifyChange;
import org.zkoss.zul.Confirmpopup;

public class F104_ZK_6097_ConfirmpopupVM {

	private String result = "pending";
	private String message = "Delete this record?";

	@Init
	public void init() {
	}

	public String getResult() { return result; }
	public String getMessage() { return message; }

	@Command
	@NotifyChange("result")
	public void onOK() {
		result = "confirmed";
	}

	@Command
	@NotifyChange("result")
	public void onCancel() {
		result = "cancelled";
	}

	@Command
	@NotifyChange("message")
	public void changeMessage() {
		message = "Are you sure?";
	}
}
