/* F95_ZK_4497Test.java

	Purpose:
		
	Description:
		
	History:
		Fri Aug 07 12:42:24 CST 2020, Created by rudyhuang

Copyright (C) 2020 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.Keys;

import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

/**
 * @author rudyhuang
 */
public class F95_ZK_4497Test extends WebDriverTestCase {
	@Test
	public void testDelete() {
		connect();

		click(jq("@searchbox"));
		waitResponse();
		// Click again to close the popup
		click(jq("@searchbox"));
		waitResponse();

		sendKeyAndExpected(Keys.DELETE, "true");
	}

	private void sendKeyAndExpected(Keys key, String expectedLog) {
		getActions().sendKeys(key).perform();
		waitResponse();

		click(jq("@button:eq(0)"));
		waitResponse();
		Assertions.assertEquals(expectedLog, getZKLog());
	}

	@Test
	public void testBackspace() {
		connect();

		click(jq("@searchbox"));
		waitResponse();
		click(jq("@searchbox"));
		waitResponse();

		sendKeyAndExpected(Keys.BACK_SPACE, "true");
	}

	@Test
	public void testClick() {
		connect();

		click(widget("@searchbox").$n("clear"));
		waitResponse();

		click(jq("@button:eq(0)"));
		waitResponse();
		Assertions.assertEquals("true", getZKLog());
	}

	@Test
	public void testShouldHaveNoEffectIfOpened() {
		connect();

		click(jq("@searchbox"));
		waitResponse();

		sendKeyAndExpected(Keys.DELETE, "false");
	}

	@Test
	public void testShouldNotAppearCloseIfOpened() {
		connect();

		JQuery closeBtn = jq(widget("@searchbox").$n("clear"));
		click(closeBtn);
		waitResponse();

		click(jq("@searchbox"));
		waitResponse();
		click(jq(".z-searchbox-item:eq(0)"));
		waitResponse();
		Assertions.assertFalse(closeBtn.isVisible());

		click(jq("@searchbox"));
		waitResponse();
		Assertions.assertTrue(closeBtn.isVisible());
	}
}
