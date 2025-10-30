/* B86_ZK_4300Test.java

	Purpose:
		
	Description:
		
	History:
		Tue May 28 12:07:32 CST 2019, Created by rudyhuang

Copyright (C) 2019 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;
import org.zkoss.test.webdriver.ztl.Widget;

/**
 * @author rudyhuang
 */
public class B86_ZK_4300Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();

		testDatebox(widget("@datebox:eq(0)"));
		testDatebox(widget("@datebox:eq(1)"));
	}

	private void testDatebox(Widget datebox) {
		click(datebox.$n("btn"));
		waitResponse();

		JQuery cal = jq("@calendar:visible");

		click(cal.find(".z-calendar-title")); // year
		waitResponse(true);
		click(cal.find(".z-calendar-title")); // decade
		waitResponse(true);
		click(cal.find(".z-calendar-left"));
		waitResponse(true);
		click(cal.find(".z-calendar-left")); // ensure to 1900
		waitResponse(true);

		click(cal.find(".z-calendar-cell[data-value=1900]")); // 1900-1909
		waitResponse(true);
		click(cal.find(".z-calendar-cell[data-value=1900]")); // 1900
		waitResponse(true);
		click(cal.find(".z-calendar-cell[data-value=0]")); // Jan
		waitResponse(true);
		click(cal.find(".z-calendar-cell:contains(1):first")); // 1
		waitResponse();

		click(datebox.$n("btn"));
		waitResponse();

		Assertions.assertEquals("1900", jq(cal.toWidget().$n("ty")).text());
		assertThat(getZKLog(), containsString("1900"));
		closeZKLog();
	}
}
