/* B85_ZK_3898VM.java

	Purpose:
		
	Description:
		
	History:
		Tue Mar 13 11:35:42 CST 2018, Created by rudyhuang

Copyright (C) 2018 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.test2;

import org.zkoss.bind.annotation.BindingParam;
import org.zkoss.bind.annotation.Command;
import org.zkoss.zul.Menuitem;
import org.zkoss.zul.Menupopup;
import org.zkoss.zul.impl.XulElement;

/**
 * @author rudyhuang
 */
public class B85_ZK_3898VM {
	private Menupopup menu;
	private Menupopup menu2;

	@Command
	public void doOnePp(@BindingParam("tg") XulElement tg) {
		if (menu == null) {
			menu = new Menupopup();
			menu.appendChild(new Menuitem("Option 1"));
			menu.setPage(tg.getPage());
		}
		tg.setContext(menu);
	}

	@Command
	public void doTwoPp(@BindingParam("tg") XulElement tg) {
		if (menu2 == null) {
			menu2 = new Menupopup();
			menu2.appendChild(new Menuitem("Option 2"));
			menu2.setPage(tg.getPage());
		}
		tg.setContext(menu2);
	}
}
