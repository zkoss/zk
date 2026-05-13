/* F104_ZK_6097_BadgeComposer.java

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
import org.zkoss.zul.Badge;
import org.zkoss.zul.Label;

public class F104_ZK_6097_BadgeComposer extends SelectorComposer<Component> {

	@Wire
	private Badge bdgMvc;
	@Wire
	private Label mvcCount;

	@Listen("onClick = #btnIncrementMvc")
	public void increment() {
		bdgMvc.setCount(bdgMvc.getCount() + 1);
		mvcCount.setValue(String.valueOf(bdgMvc.getCount()));
	}

	@Listen("onClick = #btnResetMvc")
	public void reset() {
		bdgMvc.setCount(0);
		mvcCount.setValue("0");
	}

	@Listen("onClick = #btnSetDangerMvc")
	public void setDanger() {
		bdgMvc.setSeverity("danger");
	}
}
