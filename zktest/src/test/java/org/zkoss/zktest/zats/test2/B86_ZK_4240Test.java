/* B86_ZK_4240Test.java

	Purpose:
		
	Description:
		
	History:
		Thu May 09 11:29:41 CST 2019, Created by rudyhuang

Copyright (C) 2019 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.not;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.Element;
import org.zkoss.test.webdriver.ztl.JQuery;
import org.zkoss.test.webdriver.ztl.Widget;

/**
 * @author rudyhuang
 */
public class B86_ZK_4240Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();

		Widget panel = widget("@panel:first");
		click(panel.$n("exp"));
		sleep(100);

		int contentHeight = jq("@panelchildren:first").outerHeight();
		assertThat(jq(panel.$n("body")).height(), not(greaterThan(contentHeight)));
	}

	@Test
	public void testVerticalScroll() {
		connect();

		Element btnDown = widget("@tabbox").$n("down");
		for (int i = 0; i < 100; i++) {
			click(btnDown);
			sleep(100);
		}
		JQuery tab99 = jq("@tab:contains(99)");
		click(tab99);
		waitResponse();
		Assertions.assertTrue(tab99.hasClass("z-tab-selected"));
	}
}
