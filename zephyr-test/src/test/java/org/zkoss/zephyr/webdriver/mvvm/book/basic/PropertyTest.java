/* PropertyTest.java
	Purpose:

	Description:

	History:
		Tue Oct 05 11:11:58 CST 2021, Created by jameschu

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zephyr.webdriver.mvvm.book.basic;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.Keys;

import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

/**
 * @author jameschu
 */
public class PropertyTest extends WebDriverTestCase {
	@Test
	public void test() {
		connect("/mvvm/book/basic/property.zul");
		JQuery t1 = jq("$t1");
		JQuery l1 = jq("$l1");
		JQuery l1x = jq("$l1x");
		String str = "AXX";
		assertEquals("A", t1.val());
		assertEquals("A", l1.text());
		assertEquals("", l1x.text());
		sendKeys(t1, Keys.END, "XX", Keys.TAB);
		waitResponse();
		assertEquals(str, t1.val());
		assertEquals(str, l1.text());
		assertEquals("", l1x.text());
		click(jq("$cmd1"));
		waitResponse();
		assertEquals(str, t1.val());
		assertEquals(str, l1.text());
		assertEquals(str, l1x.text());
		//test 2
		JQuery t2 = jq("$t2");
		JQuery l2 = jq("$l2");
		JQuery l2x = jq("$l2x");
		assertEquals("", t2.val());
		assertEquals("B", l2.text());
		assertEquals("", l2x.text());
		type(t2, "YY");
		waitResponse();
		assertEquals("YY", t2.val());
		assertEquals("YY", l2.text());
		assertEquals("", l2x.text());
		click(jq("$cmd2"));
		waitResponse();
		assertEquals("YY-by-cmd2", t2.val());
		assertEquals("YY-by-cmd2", l2.text());
		assertEquals("YY-by-cmd2", l2x.text());
		//test 3
		JQuery t3 = jq("$t3");
		JQuery l3 = jq("$l3");
		JQuery l3x = jq("$l3x");
		str = "CZZ";
		assertEquals("C", t3.val());
		assertEquals("", l3.text());
		assertEquals("", l3x.text());
		sendKeys(t3, Keys.END, "ZZ");
		waitResponse();
		assertEquals(str, t3.val());
		assertEquals("", l3.text());
		assertEquals("", l3x.text());
		click(jq("$cmd3"));
		waitResponse();
		assertEquals(str + "-by-cmd3", t3.val());
		assertEquals(str, l3.text());
		assertEquals(str + "-by-cmd3", l3x.text());
		type(t3, "GG");
		waitResponse();
		assertEquals("GG", t3.val());
		assertEquals(str, l3.text());
		assertEquals(str + "-by-cmd3", l3x.text());
		click(jq("$change3"));
		waitResponse();
		assertEquals(str + "-by-cmd3-by-change3", t3.val());
		assertEquals(str, l3.text());
		assertEquals(str + "-by-cmd3", l3x.text());
	}
}
