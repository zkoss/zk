/* B70_ZK_2305Test.java

	Purpose:
		
	Description:
		
	History:
		Thu Apr 25 14:10:58 CST 2019, Created by rudyhuang

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
public class B70_ZK_2305Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();

		testListbox(jq("@listbox:eq(0)"));
		testListbox(jq("@listbox:eq(1)"));
	}

	private void testListbox(JQuery lb) {
		getActions().moveToElement(toElement(lb.find(".z-listbox-body"))).perform();
		waitResponse();
		for (int i = 0; i < 4; i++) {
			trigger(lb.find(".z-scrollbar-down"), "mousedown");
			waitResponse();
		}
		click(lb.find("@listitem:last"));
		waitResponse();
		Assertions.assertTrue(lb.find("@listitem:last").hasClass("z-listitem-selected"));
	}
}
