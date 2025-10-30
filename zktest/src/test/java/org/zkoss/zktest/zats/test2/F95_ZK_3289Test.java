/* F95_ZK_3289Test.java

	Purpose:
		
	Description:
		
	History:
		Fri Oct 23 17:11:27 CST 2020, Created by rudyhuang

Copyright (C) 2020 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

/**
 * @author rudyhuang
 */
public class F95_ZK_3289Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();

		final JQuery calendarBody = jq(widget("@calendar:visible").$n("mid"));
		click(widget("$dbY").$n("btn"));
		waitResponse();
		Assertions.assertTrue(calendarBody.hasClass("z-calendar-year"),
				"It should be a year select dialog");
		click(jq(".z-calendar-cell[data-value=\"2021\"]:visible"));
		waitResponse();

		click(widget("$dbYM").$n("btn"));
		waitResponse();
		Assertions.assertTrue(calendarBody.hasClass("z-calendar-month"),
				"It should be a month select dialog");
		click(jq(".z-calendar-cell[data-value=\"1\"]:visible"));
		waitResponse();

		click(widget("$dbYMD").$n("btn"));
		waitResponse();
		click(jq(".z-calendar-cell:contains(14):visible"));
		waitResponse();

		click(jq("@button"));
		waitResponse();
		Assertions.assertEquals("2021 - 2020-02 - 2020-10-14", getZKLog());
	}
}
