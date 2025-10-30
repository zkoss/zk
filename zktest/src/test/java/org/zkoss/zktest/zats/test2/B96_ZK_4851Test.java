/* B96_ZK_4851Test.java

		Purpose:
		
		Description:
		
		History:
				Wed Apr 07 11:51:37 CST 2021, Created by leon

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;
import org.zkoss.test.webdriver.ztl.Widget;

public class B96_ZK_4851Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();

		testDatebox(widget("@datebox:eq(0)"), "07 Apr 2021");
		testDatebox(widget("@datebox:eq(1)"), "07 Apr 21");
		testDatebox(widget("@datebox:eq(2)"), "07 Apr 2021");
		testDatebox(widget("@datebox:eq(3)"), "07 Apr 2021");
		testDatebox(widget("@datebox:eq(4)"), "07 Apr 02021");
	}
	private void testDatebox(Widget datebox, String expectedDisplay) {
		click(datebox.$n("btn"));
		waitResponse();

		JQuery cal = jq("@calendar:visible");

		click(cal.find(".z-calendar-title")); // year
		waitResponse(true);
		click(cal.find(".z-calendar-title")); // decade
		waitResponse(true);
		click(cal.find(".z-calendar-right"));
		waitResponse(true);
		click(cal.find(".z-calendar-right")); // ensure to 2020
		waitResponse(true);

		click(cal.find(".z-calendar-cell[data-value=2020]")); // 2020-2029
		waitResponse(true);
		click(cal.find(".z-calendar-cell[data-value=2021]")); // 2021
		waitResponse(true);
		click(cal.find(".z-calendar-cell[data-value=3]")); // Apr
		waitResponse(true);
		click(cal.find(".z-calendar-cell:contains(7):first")); // 7
		waitResponse();

		Assertions.assertEquals(expectedDisplay, jq(datebox.$n("real")).val(), "the datebox should display " + expectedDisplay);

		click(datebox.$n("btn"));
		waitResponse();

		Assertions.assertEquals("Apr 2021", jq("@calendar:visible .z-calendar-title").text(), "calendar should show the correct year and month which is 2021 Apr");
		Assertions.assertEquals("7", jq("@calendar:visible .z-calendar-selected").text(), "calendar should select the correct day which is 7");

		click(jq("@label")); // close calendar popup
		waitResponse();
	}
}
