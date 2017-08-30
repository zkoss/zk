/* F85_ZK_3520.java

	Purpose:
		
	Description:
		
	History:
		Tue Aug 22 12:03:39 CST 2017, Created by wenninghsu

Copyright (C) 2017 Potix Corporation. All Rights Reserved.

This program is distributed under LGPL Version 2.1 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
package org.zkoss.zktest.test2;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Button;
import org.zkoss.zul.Div;
import org.zkoss.zul.Popup;

/**
 * 
 * @author wenninghsu
 */
public class F85_ZK_3520 extends SelectorComposer<Component> {

	@Wire
	Popup pop;

	@Wire
	Div div1;

	@Wire
	Div div2;

	@Wire
	Div div3;

	@Wire
	Button btn1;

	@Override
	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);
		div1.setTooltipAttributes(pop, "after_start", "(zk.currentPointer[0] + 40)", "(zk.currentPointer[1])", 100);
		div2.setPopupAttributes(pop, null, "(zk.currentPointer[0])", "(zk.currentPointer[1] + 20)", "toggle");
		div3.setContextAttributes(pop, null, "(zk.currentPointer[0] - 50)", "(zk.currentPointer[1])", null);
	}

	@Listen("onClick = button#btn1")
	public void clickBtn1() {
		div1.setTooltipAttributes(pop, null, "(zk.currentPointer[0] + 40)", "(zk.currentPointer[1])", 100);
		div2.setPopupAttributes(pop, "after_center", "(zk.currentPointer[0])", "(zk.currentPointer[1] + 120)", "toggle");
		div3.setContextAttributes(pop, null, "(zk.currentPointer[0] - 50)", "(zk.currentPointer[1])", "toggle");
	}

}
