/* F104_ZK_6097_ChipComposer.java

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
import org.zkoss.zul.Label;
import org.zkoss.zul.Chip;

public class F104_ZK_6097_ChipComposer extends SelectorComposer<Component> {

	@Wire
	private Chip chipMvc;
	@Wire
	private Label mvcResult;
	@Wire
	private Label mvcLabel;

	@Listen("onClose = #chipMvc")
	public void onTagClose() {
		mvcResult.setValue("closed");
	}

	@Listen("onClick = #btnChangeSeverity")
	public void changeSeverity() {
		chipMvc.setSeverity("success");
	}

	@Listen("onClick = #btnChangeLabel")
	public void changeLabel() {
		chipMvc.setLabel("Changed by Composer");
		mvcLabel.setValue(chipMvc.getLabel());
	}
}
