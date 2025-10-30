/* B86_ZK_4041Composer.java

        Purpose:
                
        Description:
                
        History:
                Thu Aug 30 12:58:51 CST 2018, Created by charlesqiu

Copyright (C) 2018 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.test2;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Menu;
import org.zkoss.zul.Menuitem;
import org.zkoss.zul.Menupopup;

public class B86_ZK_4041Composer extends SelectorComposer {

	@Wire
	private Menu menu;

	@Override
	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);
		menu.appendChild(generateChildren(10, 3));
	}

	private Menupopup generateChildren(int itemCount, int level) {
		Menupopup menupopup = new Menupopup();
		for (int i = 0; i < itemCount; i++)
			menupopup.appendChild(new Menuitem("menuitem"));

		if (level == 1)
			return menupopup;

		Menu subMenu = new Menu("menu");
		subMenu.appendChild(generateChildren(itemCount, level - 1));
		menupopup.appendChild(subMenu);
		return menupopup;
	}

	@Listen("onClick = #button")
	public void increaseItemCount() {
		menu.getMenupopup().detach();
		menu.appendChild(generateChildren(30, 3));
	}
}
