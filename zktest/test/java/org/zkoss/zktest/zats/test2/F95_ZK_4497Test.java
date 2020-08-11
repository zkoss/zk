/* F95_ZK_4497Test.java

	Purpose:
		
	Description:
		
	History:
		Fri Aug 07 12:42:24 CST 2020, Created by rudyhuang

Copyright (C) 2020 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.Keys;

import org.zkoss.zktest.zats.WebDriverTestCase;
import org.zkoss.zktest.zats.ztl.JQuery;

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
		sendKeys(jq("@searchbox"), key);
		waitResponse();

		click(jq("@button:eq(0)"));
		waitResponse();
		Assert.assertEquals(expectedLog, getZKLog());
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
		Assert.assertEquals("true", getZKLog());
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
		Assert.assertFalse(closeBtn.isVisible());

		click(jq("@searchbox"));
		waitResponse();
		Assert.assertTrue(closeBtn.isVisible());
	}
}
