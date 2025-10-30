/* B86_ZK_4020RowRenderer.java

        Purpose:
                
        Description:
                
        History:
                Thu Aug 09 10:29:29 CST 2018, Created by klyve

Copyright (C) 2018 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.test2;

import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Label;
import org.zkoss.zul.Row;
import org.zkoss.zul.RowRenderer;

public class B86_ZK_4020RowRenderer implements RowRenderer<Object> {

	public void render(final Row row, final Object data, int index) {
		Clients.log("render Row number " + ++B86_ZK_4020Composer.rowCounter );
		final B86_ZK_4020Composer.MyItem item = (B86_ZK_4020Composer.MyItem) data;

		new Label(item.v1).setParent(row);
		new Label(item.v2).setParent(row);
		new Label(String.valueOf(item.v3)).setParent(row);
	}
}
