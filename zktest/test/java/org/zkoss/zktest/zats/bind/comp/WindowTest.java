/* WindowTest.java

	Purpose:
		
	Description:
		
	History:
		Wed Apr 28 10:52:22 CST 2021, Created by rudyhuang

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.bind.comp;

import org.junit.Assert;
import org.junit.Test;

import org.zkoss.zktest.zats.WebDriverTestCase;
import org.zkoss.zktest.zats.ztl.JQuery;

/**
 * @author rudyhuang
 */
public class WindowTest extends WebDriverTestCase {
	@Test
	public void testMaximize() {
		connect();

		final JQuery maximizeBtn = jq(".z-window-maximize");
		final JQuery maximized = jq("$maximized");

		click(maximizeBtn);
		waitResponse(true);
		Assert.assertEquals("true", maximized.text());
		click(maximizeBtn);
		waitResponse(true);
		Assert.assertEquals("false", maximized.text());
	}

	@Test
	public void testZIndex() {
		connect();

		click(jq("$window2ZIndexAdd"));
		waitResponse();
		Assert.assertEquals("4", jq("$window2Zindex").text());
		click(jq("$window1ZIndexAdd"));
		waitResponse();
		Assert.assertEquals("4", jq("$window1Zindex").text());
	}
}
