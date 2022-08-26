/* B86_ZK_4285Test.java

	Purpose:
		
	Description:
		
	History:
		Tue May 14 10:41:03 CST 2019, Created by rudyhuang

Copyright (C) 2019 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.Widget;

/**
 * @author rudyhuang
 */
public class B86_ZK_4285Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		waitResponse();

		testVScroll(widget("@listbox:eq(0)"));
		testVScroll(widget("@listbox:eq(1)"));
		testVScroll(widget("@listbox:eq(2)"));
	}

	private void testVScroll(Widget widget) {
		boolean hasVScroll = Boolean.valueOf(zk(jq(widget.$n("body"))).eval("hasVScroll()"));
		Assertions.assertFalse(hasVScroll, "Vertical scroll appeared");
	}
}
