/* B110_ZK_5515Test.java

	Purpose:

	Description:

	History:
		Thu Oct 15 11:31:32 CST 2026, Created by peggypeng

Copyright (C) 2026 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.Keys;

import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.Widget;

@Tag("WcagTestOnly")
public class B110_ZK_5515Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		Widget column2 = widget("$column2");

		sendKeys(jq("$calendar"), Keys.SPACE);
		waitResponse();
		assertNoJSError();
		Assertions.assertTrue(jq(".z-portallayout-popup-nav:visible").eq(0).is(":focus"),
				"Space should open the menu with focus on its first visible item");

		click(jq(".z-portallayout-popup-nav:visible").eq(0));
		waitResponse();
		Assertions.assertEquals("calendar", column2.firstChild().id(), "Calendar should move to the second column");

		sendKeys(jq("$translate"), Keys.SPACE);
		waitResponse();
		getActions().sendKeys(Keys.ENTER).perform();
		waitResponse();
		Assertions.assertEquals(2, column2.nChildren());
		Assertions.assertEquals("translate", column2.lastChild().id(),
				"Translate should move to the end of the second column");

		sendKeys(jq("$translate").find(".z-panel-expand"), Keys.SPACE);
		waitResponse();
		Assertions.assertFalse(jq(".z-portallayout-popup-open").exists(),
				"Space on a header button should not open the menu");
		Assertions.assertFalse(widget("$translate").is("open"),
				"Space on the collapse button should collapse Translate");
		assertNoJSError();
	}
}
