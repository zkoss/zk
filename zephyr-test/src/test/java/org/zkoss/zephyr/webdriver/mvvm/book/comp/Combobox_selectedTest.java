/* Combobox_selectedTest.java

	Purpose:
		
	Description:
		
	History:
		Tue Apr 27 11:04:23 CST 2021, Created by rudyhuang

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
public class Combobox_selectedTest extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		JQuery select = jq("@select"); //listbox select mold
		JQuery comboboxInp = jq(".z-combobox-input");
		assertEquals("1", select.toWidget().get("selectedIndex"));
		assertEquals("Item B", comboboxInp.val());

		new Select(toElement(jq(".z-select"))).selectByVisibleText("Item A");
		waitResponse();
		assertEquals("Item A", comboboxInp.val());

		click(jq(".z-combobox-button"));
		waitResponse();
		click(jq(".z-comboitem").eq(2));
		waitResponse();
		assertEquals("2", select.toWidget().get("selectedIndex"));
	}
}
