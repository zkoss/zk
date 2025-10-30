/* F85_ZK_3506.java

	Purpose:
		
	Description:
		
	History:
		Tue Aug  8 17:25:52 CST 2017, Created by wenninghsu

Copyright (C) 2017 Potix Corporation. All Rights Reserved.

This program is distributed under LGPL Version 2.1 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
package org.zkoss.zktest.test2;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.KeyEvent;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Window;

/**
 * 
 * @author wenninghsu
 */
public class F85_ZK_3506Composer extends SelectorComposer<Component> {

	@Wire
	Window w;

	@Override
	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);
	}

	@Listen("onCtrlKey = window#w")
	public void doSomething(KeyEvent evt) {
		String msg = "";
		int keyCode = evt.getKeyCode();
		boolean ctrl = evt.isCtrlKey(),
				alt = evt.isAltKey(),
				meta = evt.isMetaKey(),
				shift = evt.isShiftKey();
		if (ctrl)
			msg += "Ctrl ";
		if (alt)
			msg += "Alt ";
		if (shift)
			msg += "Shift ";
		if (meta)
			msg += "Command ";
		switch (keyCode) {
			case 74:
				msg += "J";
				break;
			case 89:
				msg += "Y";
				break;
			case 84:
				msg += "T";
				break;
			case 79:
				msg += "O";
				break;
			case 75:
				msg += "K";
				break;
			case 80:
				msg += "P";
				break;
		}
		Clients.log("Key Pressed: " + msg);
	}

}
