/* F85_ZK_3683Test.java

	Purpose:
		
	Description:
		
	History:
		Wed Jun 19 12:28:07 CST 2019, Created by rudyhuang

Copyright (C) 2019 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

/**
 * @author rudyhuang
 */
public class F85_ZK_3683Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();

		JQuery splitter = jq("@splitlayout .z-splitlayout-splitter");
		JQuery win = jq("@window");
		int win1H = win.eq(1).height();
		int win2H = win.eq(2).height();

		// offset 20px right since the center is the button which can't be dragged
		getActions().moveToElement(toElement(splitter), 20, 0)
				.clickAndHold()
				.moveByOffset(0, -50)
				.release()
				.perform();
		waitResponse();
		Assertions.assertEquals(win1H - 50, win.eq(1).height(), 2);
		Assertions.assertEquals(win2H + 50, win.eq(2).height(), 2);

		click(splitter.find(".z-splitlayout-splitter-button"));
		waitResponse();
		Assertions.assertFalse(win.eq(1).isVisible(),
				"Window 1 should be hidden");

		click(splitter.find(".z-splitlayout-splitter-button"));
		waitResponse();
		Assertions.assertTrue(win.eq(1).isVisible(),
				"Window 1 should be visible");

		click(jq("@button"));
		waitResponse();
		Assertions.assertEquals(win.length(), 4, "There are 4 windows now. (including outer window)");
	}
}
