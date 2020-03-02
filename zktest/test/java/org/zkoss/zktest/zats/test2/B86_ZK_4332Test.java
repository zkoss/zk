/* B86_ZK_4332Test.java

	Purpose:

	Description:

	History:
		Fri Aug 16 14:27:57 CST 2019, Created by rudyhuang

Copyright (C) 2019 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.Assert;
import org.junit.Test;

import org.zkoss.zktest.zats.WebDriverTestCase;
import org.zkoss.zktest.zats.ztl.Widget;

/**
 * @author rudyhuang
 */
public class B86_ZK_4332Test extends WebDriverTestCase {
	@Test
	public void testShortFormat() {
		connect();
		checkDatebox(widget("@datebox:eq(0)"));
	}

	@Test
	public void testLongFormat() {
		connect();
		checkDatebox(widget("@datebox:eq(1)"));
	}

	private void checkDatebox(Widget db) {
		click(db.$n("btn"));
		waitResponse();

		Widget cal = widget("@calendar:visible");
		click(cal.$n("ty"));
		waitResponse(true);
		click(cal.$n("tyd"));
		waitResponse(true);
		click(cal.$n("left"));
		waitResponse(true);
		click(cal.$n("de1")); // 2443-2452
		waitResponse(true);
		click(cal.$n("y1")); // 2443
		waitResponse(true);
		click(cal.$n("m0")); // ม.ค.
		waitResponse(true);
		click(jq(cal.$n("w0")).find(".z-calendar-cell:contains(1)")); // 1
		waitResponse();

		click(db.$n("btn"));
		waitResponse();
		String calendarYear = toElement(cal.$n("ty")).getText();
		Assert.assertEquals("2443", calendarYear);
	}
}
