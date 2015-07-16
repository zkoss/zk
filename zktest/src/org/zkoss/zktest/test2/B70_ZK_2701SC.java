/* B70_ZK_2701SC.java

	Purpose:
		
	Description:
		
	History:
		Wed Jul 15 14:28:48 CST 2015, Created by chunfu

Copyright (C)  Potix Corporation. All Rights Reserved.

This program is distributed under LGPL Version 2.1 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
package org.zkoss.zktest.test2;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.MouseEvent;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;

/**
 * 
 * @author chunfu
 */
public class B70_ZK_2701SC extends SelectorComposer<Component> {
	transient int activationCount = 0;
	transient int passivationCount = 0;

	@Listen("onClick=#btn1")
	public void onClickBtn1(MouseEvent event) {

	}

	@Listen("onClick=#btn2")
	public void onClickBtn2(MouseEvent event) {

	}

	@Listen("onClick=#btn3")
	public void onClickBtn3(MouseEvent event) {

	}

	@Override
	public void didActivate(Component comp) {
		super.didActivate(comp);
		System.out.printf("SC (%d) activate: %s, %s", activationCount++, comp, comp.getId());
		System.out.println();
	}

	@Override
	public void willPassivate(Component comp) {
		super.willPassivate(comp);
		System.out.printf("SC (%d) passivate: %s, %s", passivationCount++, comp, comp.getId());
		System.out.println();
	}
}
