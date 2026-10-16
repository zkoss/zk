/* F110_ZK_6085_PortallayoutPopupColumnsTest.java

	Purpose:

	Description:

	History:
		Fri Oct 16 12:01:32 CST 2026, Created by peggypeng

Copyright (C) 2026 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.Keys;

import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

/**
 * Portallayout's move popup lists the columns the portallayout has when it opens, including one
 * added after the portallayout rendered and not one removed since.
 */
@Tag("WcagTestOnly")
public class F110_ZK_6085_PortallayoutPopupColumnsTest extends WebDriverTestCase {
	private void open() {
		sendKeys(jq("$mail"), Keys.SPACE);
		waitResponse();
	}

	private void close() {
		getActions().sendKeys(Keys.ESCAPE).perform();
		waitResponse();
	}

	private JQuery columns() {
		return jq(".z-portallayout-popup-open .z-portallayout-popup-nav:visible");
	}

	@Test
	public void test() {
		connect();
		open();
		Assertions.assertEquals(2, columns().length());
		Assertions.assertEquals("Left", columns().eq(0).text());
		Assertions.assertEquals("Right", columns().eq(1).text());
		close();

		click(jq("$addColumn"));
		waitResponse();
		open();
		Assertions.assertEquals(3, columns().length(), "A column added later should be listed");
		Assertions.assertEquals("Later", columns().eq(2).text());
		close();

		click(jq("$removeColumn"));
		waitResponse();
		open();
		Assertions.assertEquals(2, columns().length(), "A removed column should not be listed");
		Assertions.assertEquals("Left", columns().eq(0).text());
		Assertions.assertEquals("Later", columns().eq(1).text());
		getActions().sendKeys(Keys.ARROW_DOWN, Keys.ENTER).perform();
		waitResponse();
		Assertions.assertEquals("mail", jq("$later").toWidget().firstChild().id(), "Mail should move to the added column");
		assertNoAnyError();
	}
}
