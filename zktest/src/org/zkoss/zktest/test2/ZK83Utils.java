/* ZK83Utils.java

	Purpose:
		
	Description:
		
	History:
		Thu Sep 29 17:37:40 TST 2011, Created by tomyeh

Copyright (C) 2011 Potix Corporation. All Rights Reserved.

*/
package org.zkoss.zktest.test2;

import org.zkoss.zk.ui.event.EventListener;

import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Messagebox.Button;
import org.zkoss.zul.Messagebox.ClickEvent;

/**
 * Test ZK-83
 * @author tomyeh
 */
public class ZK83Utils {
	private static EventListener<ClickEvent> _listener =
		new EventListener<ClickEvent>() {
			public void onEvent(ClickEvent event) {
				Messagebox.show("Clicked "+event.getName()+":"+event.getButton());
			}
		};
	public static final void test0(org.zkoss.zul.Button btn) {
		Messagebox.show(btn.getLabel(), null, _listener);
	}
	public static final void test1(org.zkoss.zul.Button btn) {
		Messagebox.show(btn.getLabel(), new Button[] {Button.CANCEL, Button.OK}, _listener);
	}
	public static final void test2(org.zkoss.zul.Button btn) {
		Messagebox.show(btn.getLabel(), "Test2",
			new Button[] {Button.CANCEL, Button.OK, Button.NO},
			Messagebox.INFORMATION, Button.OK, _listener);
	}
}
