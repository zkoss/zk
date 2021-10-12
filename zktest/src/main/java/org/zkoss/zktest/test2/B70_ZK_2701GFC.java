/* B70_ZK_2701GFC.java

	Purpose:
		
	Description:
		
	History:
		Wed Jul 15 14:28:43 CST 2015, Created by chunfu

Copyright (C)  Potix Corporation. All Rights Reserved.

This program is distributed under LGPL Version 2.1 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
package org.zkoss.zktest.test2;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.MouseEvent;
import org.zkoss.zk.ui.util.GenericForwardComposer;

/**
 * 
 * @author chunfu
 */
public class B70_ZK_2701GFC extends GenericForwardComposer<Component> {
	transient int activationCount = 0;
	transient int passivationCount = 0;

	public void onClick$btn1(MouseEvent event) {

	}

	public void onClick$btn2(MouseEvent event) {

	}

	public void onClick$btn3(MouseEvent event) {

	}

	@Override
	public void didActivate(Component comp) {
		super.didActivate(comp);
		System.out.printf("GFC (%d) activate: %s, %s", activationCount++, comp, comp.getId());
		System.out.println();
	}

	@Override
	public void willPassivate(Component comp) {
		super.willPassivate(comp);
		System.out.printf("GFC (%d) passivate: %s, %s", passivationCount++, comp, comp.getId());
		System.out.println();
	}
}
