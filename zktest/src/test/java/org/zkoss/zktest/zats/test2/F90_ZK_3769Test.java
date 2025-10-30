/* F90_ZK_3769Test.java

	Purpose:
		
	Description:
		
	History:
		Fri Aug 16 11:12:59 CST 2019, Created by rudyhuang

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
public class F90_ZK_3769Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();

		final Widget db = widget("@datebox:eq(0)");
		checkDatebox(db, "2017", "Oct", "13", "AM 00:01:00");

		click(jq("@button"));
		waitResponse();
		checkDatebox(db, "2012", "Dec", "21", "PM 11:22:33");
	}

	@Test
	public void testDefaultUnset() {
		connect();

		final Widget db = widget("@datebox:eq(1)");
		click(db.$n("btn"));
		waitResponse();

		final String time = jq(db.$n("pp")).find(".z-timebox-input").val();
		click(db.$n("btn"));
		sleep(1500);

		click(db.$n("btn"));
		waitResponse();
		Assertions.assertNotEquals(time, jq(db.$n("pp")).find(".z-timebox-input").val());
	}

	private void checkDatebox(Widget db, String year, String month, String day, String time) {
		click(db.$n("btn"));
		waitResponse();

		final Widget cal = widget("@calendar");
		Assertions.assertEquals(year, jq(cal.$n("ty")).text());
		Assertions.assertEquals(month, jq(cal.$n("tm")).text());
		Assertions.assertEquals(day, jq(cal.$n()).find(".z-calendar-selected").text());
		Assertions.assertEquals(time, jq(db.$n("pp")).find(".z-timebox-input").val());

		click(db.$n("btn"));
		waitResponse();
	}
}
