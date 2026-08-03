/* F104_ZK_6097_AvatarComposer.java

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
import org.zkoss.zul.Avatar;
import org.zkoss.zul.Label;

public class F104_ZK_6097_AvatarComposer extends SelectorComposer<Component> {

	@Wire
	private Avatar avMvc;
	@Wire
	private Label mvcShape;
	@Wire
	private Label mvcLabel;

	@Listen("onClick = #btnSquare")
	public void setSquare() {
		avMvc.setShape("square");
		mvcShape.setValue(avMvc.getShape());
	}

	@Listen("onClick = #btnLarge")
	public void setLarge() {
		avMvc.setSize("large");
	}

	@Listen("onClick = #btnChangeLabel")
	public void changeLabel() {
		avMvc.setLabel("XY");
		mvcLabel.setValue(avMvc.getLabel());
	}
}
