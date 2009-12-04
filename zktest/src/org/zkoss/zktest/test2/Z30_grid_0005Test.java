/* Z30_grid_0005Test.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Dec 4, 2009 5:47:54 PM , Created by jumperchen
}}IS_NOTE

Copyright (C) 2009 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
}}IS_RIGHT
*/
package org.zkoss.zktest.test2;

import org.junit.Test;
import org.zkoss.zktest.Element;
import org.zkoss.zktest.Jquery;
import org.zkoss.zktest.Widget;
import org.zkoss.zktest.ZKClientTestCase;

import com.thoughtworks.selenium.Selenium;

/**
 * @author jumperchen
 *
 */
public class Z30_grid_0005Test extends ZKClientTestCase {
	public Z30_grid_0005Test() {
		target = "Z30-grid-0005.zul";
	}
	
	@Test(expected = AssertionError.class)
	public void testDisplay() {
		Widget checkbox = widget(3);
		Widget grid = widget(4);
		String checkId = checkbox.uuid() + "-real";
		Widget column = widget(12);
		Element $column = column.$n();
		Jquery $jq = jq($column);
		String uuid = column.uuid();
		Element $head = grid.$n("head");
		Element $body = grid.$n("body");
		
		Widget head1 = widget(5);
		Widget head2 = head1.nextSibling();
		Widget head3 = head2.nextSibling();
		Widget head4 = head3.nextSibling();
		for (Selenium browser : browsers) {
			try {
				start(browser);

				int c1 = Integer.parseInt(head1.firstChild().$n().get("colSpan"));
				int c2 = Integer.parseInt(head1.lastChild().$n().get("colSpan"));
				assertEquals(3, c1 + c2);
				c1 = Integer.parseInt(head2.firstChild().$n().get("colSpan"));
				c2 = Integer.parseInt(head2.lastChild().$n().get("colSpan"));
				assertEquals(3, c1 + c2);

				c1 = Integer.parseInt(head3.firstChild().$n().get("colSpan"));
				c2 = Integer.parseInt(head3.firstChild().nextSibling().$n().get("colSpan"));
				int c3 = Integer.parseInt(head3.lastChild().$n().get("colSpan"));
				assertEquals(3, c1 + c2 + c3);
				
				c1 = Integer.parseInt(head4.firstChild().$n().get("colSpan"));
				assertEquals(3, c1);

				int w = zk(grid).revisedWidth(getElementWidth(grid.uuid()).intValue());
				assertEquals(w + "", $head.get("offsetWidth"));
				assertEquals(w + "", $body.get("offsetWidth"));
				int h = zk(grid).revisedHeight(getElementHeight(grid.uuid()).intValue());
				assertEquals(h, Integer.parseInt($head.get("offsetHeight")) + Integer.parseInt($body.get("offsetHeight")));
				
				click(checkId);
				Number width = getElementWidth(uuid);
				mouseMoveAt(uuid, width + ",0");
				assertTrue($jq.hasClass("z-column-sizing"));
				
				dragdropTo(uuid, width + ",0", width.intValue() - 20 + ",0");
				assertEquals(width.intValue() - 20, getElementWidth(uuid).intValue());
			} finally {
				stop();
			}
		}
	}
}
