/* B86_ZK_4020ListitemRenderer.java

        Purpose:
                
        Description:
                
        History:
                Thu Aug 09 17:50:19 CST 2018, Created by klyve

Copyright (C) 2018 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.test2;

import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;

public class B86_ZK_4020ListitemRenderer implements ListitemRenderer<Object> {

	public void render(final Listitem listitem, final Object data, int index) {
		Clients.log("render listitem number " + ++B86_ZK_4020Composer.itemCounter);
		final B86_ZK_4020Composer.MyItem item = (B86_ZK_4020Composer.MyItem) data;
		new Listcell(item.v1).setParent(listitem);
		new Listcell(item.v2).setParent(listitem);
		new Listcell(String.valueOf(item.v3)).setParent(listitem);
	}
}
