/* B86_ZK_4017RowRenderer.java

	Purpose:
		
	Description:
		
	History:
		Mon Aug 06 11:22:41 CST 2018, Created by rudyhuang

Copyright (C) 2018 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.test2;

import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;

/**
 * @author rudyhuang
 */
public class B86_ZK_4017ListitemRenderer implements ListitemRenderer<Object> {
	public void render(final Listitem listitem, final Object data, int index) {
		System.out.print(" " + ++B86_ZK_4017ListboxComposer.counter );
		final B86_ZK_4017MyItem item = (B86_ZK_4017MyItem) data;

		new Listcell(item.v1).setParent(listitem);
		new Listcell(item.v2).setParent(listitem);
		new Listcell(String.valueOf(item.v3)).setParent(listitem);
	}
}
