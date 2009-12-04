/* Z30_grid_0001Test.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Dec 3, 2009 12:24:21 PM , Created by jumperchen
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
public class Z30_grid_0001Test extends ZKClientTestCase {

	public Z30_grid_0001Test() {
		target = "Z30-grid-0001.zul";
	}

	@Test(expected = AssertionError.class)
	public void testAddAfter() {
		Widget rows = widget(7);
		String addEnd = uuid(14);
		for (Selenium browser : browsers) {
			try {
				start(browser);
				
				assertEquals("1", rows.eval("nChildren"));
				
				click(addEnd);
				assertEquals("2", rows.eval("nChildren"));
				assertEquals("2", rows.$n().get("rows.length"));
				assertEquals("Label x", rows.lastChild().firstChild().get("value"));
			} finally {
				stop();
			}
		}
	}
	
	@Test(expected = AssertionError.class)
	public void testAddBefore() {
		Widget rows = widget(7);
		String addBegin = uuid(15);
		for (Selenium browser : browsers) {
			try {
				start(browser);
				
				assertEquals("1", rows.eval("nChildren"));
				
				click(addBegin);
				assertEquals("2", rows.eval("nChildren"));
				assertEquals("2", rows.$n().get("rows.length"));
				assertEquals("Label 0", rows.firstChild().firstChild().get("value"));
				
			} finally {
				stop();
			}
		}
	}

	@Test(expected = AssertionError.class)
	public void testDelete() {
		Widget rows = widget(7);
		Element $n = rows.$n();
		String addBegin = uuid(15);
		String removeLast = uuid(16);
		String removeFirst = uuid(17);
		for (Selenium browser : browsers) {
			try {
				start(browser);
				
				assertEquals("1", rows.eval("nChildren"));
				
				click(addBegin);
				assertEquals("2", rows.eval("nChildren"));
				assertEquals("2", $n.get("rows.length"));
				
				click(removeLast);
				assertEquals("1", rows.eval("nChildren"));
				assertEquals("1", $n.get("rows.length"));
				assertEquals("Label 0", rows.firstChild().firstChild().get("value"));
				
				click(removeFirst);
				assertEquals("0", rows.eval("nChildren"));
				assertEquals("0", $n.get("rows.length"));
			} finally {
				stop();
			}
		}
	}

	@Test(expected = AssertionError.class)
	public void testSetWidth() {
		Widget grid = widget(2);
		Element $head = grid.$n("head");
		String width200 = uuid(20);
		String width600 = uuid(21);
		String widthNull = uuid(22);
		for (Selenium browser : browsers) {
			try {
				start(browser);
				
				assertEquals("400px", grid.get("width"));
				
				click(width200);
				assertEquals("200px", grid.get("width"));
				
				assertEquals("200px", $head.get("style.width"));
				
				click(width600);
				assertEquals("600px", grid.get("width"));
				assertEquals("600px", $head.get("style.width"));
				
				click(widthNull);
				assertEquals("null", grid.get("width"));
				assertNotEquals("", $head.get("style.width"));
			} finally {
				stop();
			}
		}
	}

	@Test(expected = AssertionError.class)
	public void testSetHeight() {
		Widget grid = widget(2);
		Element $head = grid.$n("head");
		Element $body = grid.$n("body");
		
		String height200 = uuid(23);
		String height600 = uuid(24);
		String heightNull = uuid(25);
		for (Selenium browser : browsers) {
			try {
				start(browser);
				
				assertEquals("null", grid.get("height"));
				String height = $body.get("style.height");
				
				click(height200);
				assertEquals("200px", grid.get("height"));
				
				int hh = Integer.parseInt($head.get("offsetHeight"));
				int bh = Integer.parseInt($body.get("offsetHeight"));
				assertEquals(200, hh + bh);
				
				click(height600);				
				assertEquals("600px", grid.get("height"));

				hh = Integer.parseInt($head.get("offsetHeight"));
				bh = Integer.parseInt($body.get("offsetHeight"));
				assertEquals(600, 600);
				
				
				click(heightNull);
				assertEquals("null", grid.get("height"));
				assertEquals(height, $body.get("style.height"));
			} finally {
				stop();
			}
		}
	}

	@Test(expected = AssertionError.class)
	public void testSetColumnWidth() {
		Widget column = widget(4);
		Element $column = column.$n();
		String width20 = uuid(28);
		String width100 = uuid(29);
		String width200 = uuid(30);
		
		Widget column2 = widget(5);
		Element $column2 = column2.$n();
		String width20b = uuid(33);
		String width100b = uuid(34);

		Widget column3 = widget(6);
		Element $column3 = column3.$n();
		String width20c = uuid(37);
		String width100c = uuid(38);
		for (Selenium browser : browsers) {
			try {
				start(browser);

				assertEquals("50px", column.get("width"));
				
				click(width20);
				assertEquals("20px", column.get("width"));
				assertEquals("20px", $column.get("style.width"));
				
				click(width100);
				assertEquals("100px", column.get("width"));
				assertEquals("100px", $column.get("style.width"));
				
				click(width200);
				assertEquals("200px", column.get("width"));
				assertEquals("200px", $column.get("style.width"));

				// column2 
				assertEquals("null", column2.get("width"));
				
				click(width20b);
				assertEquals("20px", column2.get("width"));
				assertEquals("20px", $column2.get("style.width"));
				
				click(width100b);
				assertEquals("100px", column2.get("width"));
				assertEquals("100px", $column2.get("style.width"));
				

				// column3 
				assertEquals("null", column3.get("width"));
				click(width20c);
				assertEquals("20px", column3.get("width"));
				assertEquals("20px", $column3.get("style.width"));
				
				click(width100c);
				assertEquals("100px", column3.get("width"));
				assertEquals("100px", $column3.get("style.width"));
			} finally {
				stop();
			}
		}
	}

	@Test(expected = AssertionError.class)
	public void testSizable() {
		Widget columns = widget(3);
		Widget column = widget(4);
		
		Element $column = column.$n();
		Jquery $jq = jq($column);
		
		String uuid = column.uuid();
		String enable = uuid(40);
		String disable = uuid(41);
		for (Selenium browser : browsers) {
			try {
				start(browser);
				
				assertFalse(columns.is("sizable"));
				
				click(enable);
				assertTrue(columns.is("sizable"));
				
				String width = $column.get("offsetWidth");
				mouseMoveAt(uuid, width + ",0");
				assertTrue($jq.hasClass("z-column-sizing"));

				click(disable);
				assertFalse(columns.is("sizable"));
				
				mouseMoveAt(uuid, width + ",0");
				assertFalse($jq.hasClass("z-column-sizing"));
				
				//drag-drop test
				click(enable);
				dragdropTo(uuid, width + ",0", Integer.parseInt(width) + 10 + ",0");
				assertEquals("60", $column.get("offsetWidth"));
			} finally {
				browser.stop();
			}
		}
	}
}
