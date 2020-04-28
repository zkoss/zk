/* B90_ZK_4556Test.java

		Purpose:
		
		Description:
		
		History:
			Mon Apr 27 12:46:50 CST 2020, Created by jameschu

Copyright (C) 2020 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import org.zkoss.zktest.zats.WebDriverTestCase;
import org.zkoss.zktest.zats.ztl.JQuery;

/**
 * @author jameschu
 */
public class B90_ZK_4556Test extends WebDriverTestCase {
	@Test
	public void test() {
		final String PADDING_TOP = "padding-top";
		final String PADDING_BOTTOM = "padding-bottom";
		final String PADDING_LEFT = "padding-left";
		final String PADDING_RIGHT = "padding-right";
		connect();
		waitResponse();
		JQuery jqBtn = jq("@button");
		click(jqBtn.eq(0));
		waitResponse();
		JQuery jqWin = jq(".z-window");
		assertEquals(jqWin.innerHeight() - parseInt(jqWin.css(PADDING_TOP)) - parseInt(jqWin.css(PADDING_BOTTOM)),
				jq(".z-window-content").outerHeight() + jq(".z-window-header").outerHeight(), 1);
		click(jq(".z-window-close"));
		waitResponse();
		click(jqBtn.eq(1));
		waitResponse();
		JQuery jqCell = jq(".z-cell");
		JQuery jqCell0 = jqCell.eq(0);
		JQuery jqCell1 = jqCell.eq(1);
		assertEquals(jqCell0.innerWidth() - parseInt(jqCell0.css(PADDING_LEFT)) - parseInt(jqCell0.css(PADDING_RIGHT)),
				jqCell0.children().width(), 1);
		assertEquals(jqCell1.innerWidth() - parseInt(jqCell1.css(PADDING_LEFT)) - parseInt(jqCell1.css(PADDING_RIGHT)),
				jqCell1.children().width(), 1);
	}
}
