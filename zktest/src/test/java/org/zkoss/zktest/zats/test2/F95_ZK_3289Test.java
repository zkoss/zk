/* F95_ZK_3289Test.java

	Purpose:
		
	Description:
		
	History:
		Fri Oct 23 17:11:27 CST 2020, Created by rudyhuang

Copyright (C) 2020 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.Assert;
import org.junit.Test;

import org.zkoss.zktest.zats.WebDriverTestCase;
import org.zkoss.zktest.zats.ztl.JQuery;

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
		Assert.assertTrue("It should be a year select dialog",
				calendarBody.hasClass("z-calendar-year"));
		click(jq(".z-calendar-cell[data-value=\"2021\"]:visible"));
		waitResponse();

		click(widget("$dbYM").$n("btn"));
		waitResponse();
		Assert.assertTrue("It should be a month select dialog",
				calendarBody.hasClass("z-calendar-month"));
		click(jq(".z-calendar-cell[data-value=\"1\"]:visible"));
		waitResponse();

		click(widget("$dbYMD").$n("btn"));
		waitResponse();
		click(jq(".z-calendar-cell:contains(14):visible"));
		waitResponse();

		click(jq("@button"));
		waitResponse();
		Assert.assertEquals("2021 - 2020-02 - 2020-10-14", getZKLog());
	}
}
