/* Z30_grid_0003Test.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Dec 4, 2009 5:22:24 PM , Created by jumperchen
}}IS_NOTE

Copyright (C) 2009 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
}}IS_RIGHT
*/
package org.zkoss.zktest.test2;

import org.junit.Test;
import org.zkoss.zktest.Element;
import org.zkoss.zktest.Widget;
import org.zkoss.zktest.ZKClientTestCase;

import com.thoughtworks.selenium.Selenium;

/**
 * @author jumperchen
 *
 */
public class Z30_grid_0003Test extends ZKClientTestCase {
	public Z30_grid_0003Test() {
		target = "Z30-grid-0003.zul";
	}
	@Test(expected = AssertionError.class)
	public void testModel() {
		Widget grid = widget(12);
		Widget paging = widget(13);
		Widget rows = widget(14);
		Element $paging = paging.$n();
		String btn1 = uuid(10);
		String btn2 = uuid(11);
		
		String uuid = paging.uuid();
		String first = uuid + "-first";
		String prev = uuid + "-prev";
		String next = uuid + "-next";
		String last = uuid + "-last";
		for (Selenium browser : browsers) {
			try {
				start(browser);
				assertEquals("400px", grid.get("width"));
				assertEquals("13", rows.eval("nChildren"));
				assertEquals("V130", rows.firstChild().firstChild().get("value"));
				assertTrue(10 < Integer.parseInt($paging.get("offsetHeight")));
				assertEquals("10", paging.get("activePage"));
				
				click(btn1);
				assertEquals("400px", grid.get("width"));
				assertEquals("13", rows.eval("nChildren"));
				assertEquals("V0", rows.firstChild().firstChild().get("value"));
				assertTrue(10 < Integer.parseInt($paging.get("offsetHeight")));
				assertEquals("10", paging.get("activePage"));
				
				click(btn2);
				assertEquals("400px", grid.get("width"));
				assertEquals("13", rows.eval("nChildren"));
				assertEquals("V0", rows.firstChild().firstChild().get("value"));
				assertNotEquals("0", $paging.get("offsetHeight"));
				assertEquals("10", paging.get("activePage"));

				click(first);
				assertEquals("13", rows.eval("nChildren"));
				assertEquals("V0", rows.firstChild().firstChild().get("value"));
				assertTrue(10 < Integer.parseInt($paging.get("offsetHeight")));
				assertEquals("0", paging.get("activePage"));
				
				click(next);
				assertEquals("13", rows.eval("nChildren"));
				assertEquals("V13", rows.firstChild().firstChild().get("value"));
				assertTrue(10 < Integer.parseInt($paging.get("offsetHeight")));
				assertEquals("1", paging.get("activePage"));

				click(prev);
				assertEquals("13", rows.eval("nChildren"));
				assertEquals("V0", rows.firstChild().firstChild().get("value"));
				assertTrue(10 < Integer.parseInt($paging.get("offsetHeight")));
				assertEquals("0", paging.get("activePage"));

				click(last);
				assertEquals("3", rows.eval("nChildren"));
				assertEquals("V9997", rows.firstChild().firstChild().get("value"));
				assertTrue(10 < Integer.parseInt($paging.get("offsetHeight")));
				assertEquals("769", paging.get("activePage"));
			} finally {
				stop();
			}
		}
	}
}
