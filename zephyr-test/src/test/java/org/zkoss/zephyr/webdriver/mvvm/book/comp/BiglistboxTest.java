/* BiglistboxTest.java

	Purpose:
		
	Description:
		
	History:
		Mon Apr 26 12:55:26 CST 2021, Created by rudyhuang

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zephyr.webdriver.mvvm.book.comp;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.support.ui.Select;

import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

/**
 * @author rudyhuang
 */
public class BiglistboxTest extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		waitResponse();
		assertEquals("0", jq(".z-biglistbox-selected").eval("index()"));

		click(jq(".z-biglistbox-row:eq(1)"));
		waitResponse();
		final JQuery lbxObject = jq("$listbox1");
		final JQuery lbxIndex = jq("$listbox2");
		assertEquals("1", lbxObject.toElement().get("selectedIndex"));
		assertEquals("1", lbxIndex.toElement().get("selectedIndex"));

		new Select(toElement(lbxObject)).selectByIndex(2);
		waitResponse();
		assertEquals("2", jq(".z-biglistbox-selected").eval("index()"));
		assertEquals("2", lbxIndex.toElement().get("selectedIndex"));

		new Select(toElement(lbxIndex)).selectByIndex(3);
		waitResponse();
		assertEquals("3", jq(".z-biglistbox-selected").eval("index()"));
		assertEquals("3", lbxObject.toElement().get("selectedIndex"));

		click(jq("@button:contains(clear)"));
		waitResponse();
		assertEquals(0, jq(".z-biglistbox-selected").length());
		assertEquals("-1", lbxObject.toElement().get("selectedIndex"));
		assertEquals("-1", lbxIndex.toElement().get("selectedIndex"));
	}
}
