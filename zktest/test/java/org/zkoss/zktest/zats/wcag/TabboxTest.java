/* TabboxTest.java

	Purpose:
		
	Description:
		
	History:
		Thu Jun 11 12:52:22 CST 2020, Created by rudyhuang

Copyright (C) 2020 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.wcag;

import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.Keys;

import org.zkoss.zktest.zats.ztl.JQuery;

/**
 * @author rudyhuang
 */
public class TabboxTest extends WcagTestCase {
	@Test
	public void test() {
		connect();
		verifyA11y();

		click(jq("@tab:contains(Tab D):eq(0)"));
		waitResponse(true);
		click(jq("@tab:contains(Tab D):eq(1)"));
		waitResponse(true);
		click(jq("@tab:contains(Tab D):eq(2)"));
		waitResponse(true);
		verifyA11y();
	}

	@Test
	public void testClickCloseFocus() {
		connect();

		click(widget("@tab:contains(Tab H):eq(0)").$n("cls"));
		waitResponse();
		Assert.assertTrue(jq(".z-tab-selected:eq(0)").is(":focus"));
	}

	@Test
	public void testKeyboardCloseFocus() {
		connect();

		JQuery tabH = jq("@tab:contains(Tab H):eq(0)");
		click(tabH);
		waitResponse(true);
		sendKeys(tabH, Keys.DELETE);
		waitResponse();
		Assert.assertTrue(jq(".z-tab-selected:eq(0)").is(":focus"));
	}
}
