/* B86_ZK_4240Test.java

	Purpose:
		
	Description:
		
	History:
		Thu May 09 11:29:41 CST 2019, Created by rudyhuang

Copyright (C) 2019 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.not;

import org.junit.Assert;
import org.junit.Test;

import org.zkoss.zktest.zats.WebDriverTestCase;
import org.zkoss.zktest.zats.ztl.Element;
import org.zkoss.zktest.zats.ztl.JQuery;
import org.zkoss.zktest.zats.ztl.Widget;

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
		Assert.assertThat(jq(panel.$n("body")).height(), not(greaterThan(contentHeight)));
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
		Assert.assertTrue(tab99.hasClass("z-tab-selected"));
	}
}
