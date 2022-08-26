/* B50_3094720Test.java

		Purpose:
                
		Description:
                
		History:
				Thu Mar 21 12:41:17 CST 2019, Created by charlesqiu

Copyright (C) 2019 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

public class B50_3094720Test extends WebDriverTestCase {

	@Test
	public void test() {
		connect();
		JQuery vlayout = jq(".z-vlayout");
		JQuery hlayout = jq(".z-hlayout");
		JQuery buttons = jq(".z-button");
		final String PADDING_BOTTOM = "padding-bottom";
		final String PADDING_RIGHT = "padding-right";

		testLayout(vlayout, PADDING_BOTTOM);
		click(buttons.eq(0));
		waitResponse();
		testLayout(vlayout, PADDING_BOTTOM);
		click(buttons.eq(1));
		waitResponse();
		testLayout(vlayout, PADDING_BOTTOM);
		click(buttons.eq(2));
		waitResponse();
		testLayout(vlayout, PADDING_BOTTOM, "20px");

		testLayout(hlayout, PADDING_RIGHT);
		click(buttons.eq(3));
		waitResponse();
		testLayout(hlayout, PADDING_RIGHT);
		click(buttons.eq(4));
		waitResponse();
		testLayout(hlayout, PADDING_RIGHT);
		click(buttons.eq(5));
		waitResponse();
		testLayout(hlayout, PADDING_RIGHT, "20px");
	}

	private void testLayout(JQuery layout, String css) {
		testLayout(layout, css, "10px");
	}

	private void testLayout(JQuery layout, String css, String spacing) {
		JQuery children = layout.children();
		int length = children.length();
		for (int i = 0; i < length; i++) {
			JQuery child = children.eq(i);
			spacing = child.is(":last-child") ? "0px" : spacing;
			Assertions.assertEquals(spacing, child.css(css));
		}
	}
}
