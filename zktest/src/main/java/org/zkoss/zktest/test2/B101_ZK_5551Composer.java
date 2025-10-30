/* B101_ZK_5551Composer.java

	Purpose:

	Description:

	History:
		11:33 AM 2024/9/25, Created by jumperchen

Copyright (C) 2024 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.test2;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Label;

/**
 * @author jumperchen
 */
public class B101_ZK_5551Composer implements org.zkoss.zk.ui.util.Composer {
	public void doAfterCompose(Component comp) {
		String itemLabel = (String) comp.getParent().getAttribute("item");
		Clients.log("executing composer for " + comp + " " + itemLabel + " page: " +comp.getPage());
		comp.appendChild(new Label("itemLabel"));
	}
}
