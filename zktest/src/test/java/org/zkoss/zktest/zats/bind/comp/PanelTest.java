/* PanelTest.java

	Purpose:
		
	Description:
		
	History:
		Tue Apr 27 14:21:42 CST 2021, Created by rudyhuang

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
public class PanelTest extends WebDriverTestCase {
	@Test
	public void testOpenMaximize() {
		connect();

		final JQuery expandBtn = jq(".z-panel-expand");
		final JQuery maximizeBtn = jq(".z-panel-maximize");
		final JQuery open = jq("$open");
		final JQuery maximized = jq("$maximized");

		click(expandBtn);
		waitResponse(true);
		Assert.assertEquals("true", open.text());
		click(expandBtn);
		waitResponse(true);
		Assert.assertEquals("false", open.text());

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

		click(jq("$bottomZIndexAdd"));
		waitResponse();
		Assert.assertEquals("4", jq("$panel1Zindex").text());
		click(jq("$topZIndexAdd"));
		waitResponse();
		Assert.assertEquals("4", jq("$panel2Zindex").text());
	}
}
