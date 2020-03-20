/* B90_ZK_4491RowRenderer.java

	Purpose:
		
	Description:
		
	History:
		Wed Mar 18 14:30:20 CST 2020, Created by jameschu

Copyright (C) 2020 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.test2;

import org.zkoss.zul.Label;
import org.zkoss.zul.Row;
import org.zkoss.zul.RowRenderer;

/**
 * @author jameschu
 */
public class B90_ZK_4491RowRenderer implements RowRenderer {
	public void render(final Row row, final java.lang.Object data) {
		String s = (String) data;
		Label l = new Label(s);
		l.setStyle("white-space: nowrap;");
		l.setParent(row);
	}
	/**
	 /* added by aaknai for compatibility with zk 6.5.2
	 */
	public void render(final Row row, final java.lang.Object data, int index) {
		render(row, data);
	}
}