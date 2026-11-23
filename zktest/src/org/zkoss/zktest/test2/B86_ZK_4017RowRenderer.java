/* B86_ZK_4017RowRenderer.java

	Purpose:
		
	Description:
		
	History:
		Mon Aug 06 11:22:41 CST 2018, Created by rudyhuang

Copyright (C) 2018 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.test2;

import org.zkoss.zul.Label;
import org.zkoss.zul.Row;
import org.zkoss.zul.RowRenderer;

/**
 * @author rudyhuang
 */
public class B86_ZK_4017RowRenderer implements RowRenderer<Object> {
	public void render(final Row row, final Object data, int index) {
		System.out.print(" " + ++B86_ZK_4017Composer.counter );
		final B86_ZK_4017MyItem item = (B86_ZK_4017MyItem) data;

		new Label(item.v1).setParent(row);
		new Label(item.v2).setParent(row);
		new Label(String.valueOf(item.v3)).setParent(row);
	}
}
