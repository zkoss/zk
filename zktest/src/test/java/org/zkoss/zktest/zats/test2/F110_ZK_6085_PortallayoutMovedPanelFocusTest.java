/* F110_ZK_6085_PortallayoutMovedPanelFocusTest.java

	Purpose:

	Description:

	History:
		Fri Oct 16 12:42:03 CST 2026, Created by peggypeng

Copyright (C) 2026 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.Keys;

import org.zkoss.test.webdriver.WebDriverTestCase;

/**
 * A panel moved with Portallayout's move popup keeps the focus, although the server replaces it.
 */
@Tag("WcagTestOnly")
public class F110_ZK_6085_PortallayoutMovedPanelFocusTest extends WebDriverTestCase {
	private void open() {
		sendKeys(jq("$mail"), Keys.SPACE);
		waitResponse();
	}

	@Test
	public void test() {
		connect();
		open();
		getActions().sendKeys(Keys.ENTER, Keys.ARROW_DOWN, Keys.ENTER).perform();
		waitResponse();
		Assertions.assertEquals("mail", widget("$right").lastChild().id(), "Mail should move to Right");
		Assertions.assertTrue(jq("$mail").is(":focus"), "The moved panel should keep the focus");

		open();
		click(jq(".z-portallayout-popup-nav").eq(0));
		waitResponse();
		Assertions.assertEquals("mail", widget("$left").firstChild().id(), "Mail should move back to Left");
		Assertions.assertTrue(jq("$mail").is(":focus"), "A panel moved by a click should keep the focus too");
		assertNoAnyError();
	}
}
