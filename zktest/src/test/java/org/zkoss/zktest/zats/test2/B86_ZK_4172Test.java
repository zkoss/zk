/* B86_ZK_4172Test.java

	Purpose:
		
	Description:
		
	History:
		Wed Jan 16 11:05:27 CST 2019, Created by rudyhuang

Copyright (C) 2019 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;

/**
 * @author rudyhuang
 */
public class B86_ZK_4172Test extends WebDriverTestCase {
	@Test
	public void testMethod1() {
		connect();
		sleep(500);

		int scrollHeight = jq("@grid > .z-grid-body").scrollHeight();
		jq("@grid > .z-grid-body").scrollTop(scrollHeight / 3 * 2); // approximately 130
		waitResponse();

		int tPadHeight = getTopPadHeight();
		click(jq("@button:eq(0)"));
		waitResponse();
		Assertions.assertEquals(tPadHeight, getTopPadHeight(), "The height of topPad has been changed.");
	}

	@Test
	public void testMethod2() {
		connect();
		sleep(500);

		int scrollHeight = jq("@grid > .z-grid-body").scrollHeight();
		jq("@grid > .z-grid-body").scrollTop(scrollHeight / 3 * 2); // approximately 130
		waitResponse();

		int tPadHeight = getTopPadHeight();
		click(jq("@button:eq(1)"));
		waitResponse();
		Assertions.assertEquals(tPadHeight, getTopPadHeight(), "The height of topPad has been changed.");
	}

	@Test
	public void testMethod3() {
		connect();
		sleep(500);

		int scrollHeight = jq("@grid > .z-grid-body").scrollHeight();
		jq("@grid > .z-grid-body").scrollTop(scrollHeight / 3 * 2); // approximately 130
		waitResponse();

		int tPadHeight = getTopPadHeight();
		click(jq("@button:eq(2)"));
		waitResponse();
		Assertions.assertEquals(tPadHeight, getTopPadHeight(), "The height of topPad has been changed.");
	}

	private int getTopPadHeight() {
		return Integer.parseInt(widget("@grid").$n("tpad").get("clientHeight"));
	}
}
