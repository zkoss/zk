/* F104_ZK_6097_ConfirmpopupComposer.java

	Purpose:

	Description:

	History:
		Wed May 13 13:05:00 CST 2026, Created by peakerlee

Copyright (C) 2026 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.test2;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Confirmpopup;
import org.zkoss.zul.Label;

public class F104_ZK_6097_ConfirmpopupComposer extends SelectorComposer<Component> {

	@Wire
	private Confirmpopup cpMvc;
	@Wire
	private Label mvcResult;

	@Listen("onOK = #cpMvc")
	public void onOK() {
		mvcResult.setValue("confirmed");
	}

	@Listen("onCancel = #cpMvc")
	public void onCancel() {
		mvcResult.setValue("cancelled");
	}

	@Listen("onClick = #btnChangeMsg")
	public void changeMessage() {
		cpMvc.setMessage("Are you really sure?");
	}
}
