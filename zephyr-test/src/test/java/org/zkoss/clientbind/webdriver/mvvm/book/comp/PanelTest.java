/* PanelTest.java

	Purpose:
		
	Description:
		
	History:
		Tue Apr 27 14:21:42 CST 2021, Created by rudyhuang

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.clientbind.webdriver.mvvm.book.comp;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import org.zkoss.clientbind.webdriver.ClientBindTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

/**
 * @author rudyhuang
 */
public class PanelTest extends ClientBindTestCase {
	@Test
	public void testOpenMaximize() {
		connect();

		final JQuery expandBtn = jq(".z-panel-expand");
		final JQuery maximizeBtn = jq(".z-panel-maximize");
		final JQuery open = jq("$open");
		final JQuery maximized = jq("$maximized");

		click(expandBtn);
		waitResponse(true);
		assertEquals("true", open.text());
		click(expandBtn);
		waitResponse(true);
		assertEquals("false", open.text());

		click(maximizeBtn);
		waitResponse(true);
		assertEquals("true", maximized.text());
		click(maximizeBtn);
		waitResponse(true);
		assertEquals("false", maximized.text());
	}

	@Test
	public void testZIndex() {
		connect();

		click(jq("$bottomZIndexAdd"));
		waitResponse();
		click(jq("$topZIndexAdd"));
		waitResponse();
		assertEquals("4", jq("$panel1Zindex").text());
		assertEquals("4", jq("$panel2Zindex").text());
	}
}
