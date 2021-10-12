/* F90_ZK_4347_errorsTest.java

	Purpose:
		
	Description:
		
	History:
		Tue Aug 13 15:41:30 CST 2019, Created by rudyhuang

Copyright (C) 2019 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.Assert;
import org.junit.Test;

import org.zkoss.zktest.zats.WebDriverTestCase;
import org.zkoss.zktest.zats.ztl.Element;
import org.zkoss.zktest.zats.ztl.Widget;

/**
 * @author rudyhuang
 */
public class F90_ZK_4347_constTest extends WebDriverTestCase {
	@Test
	public void testDateboxes() {
		connect();

		testNoPast(widget("@datebox:eq(0)"));
		testNoPast(widget("@datebox:eq(1)"));
		testNoPast(widget("@datebox:eq(2)"));

		testDateRange(widget("@datebox:eq(3)"));
		testDateRange(widget("@datebox:eq(4)"));
		testDateRange(widget("@datebox:eq(5)"));

		testDateRangeByServer(widget("@datebox:eq(6)"));
		testDateRangeByServer(widget("@datebox:eq(7)"));
	}

	@Test
	public void testTimeboxes() {
		connect();

		testTimeRange(widget("@timebox:eq(0)"));
		testTimeRange(widget("@timebox:eq(1)"));
		testTimeRangeByPick(widget("@timepicker:eq(0)"));

		testTimeBefore(widget("@timebox:eq(2)"));
		testTimeBeforeByPick(widget("@timepicker:eq(1)"));
		testTimeBeforeByPick(widget("@timepicker:eq(2)"));
	}

	private void testNoPast(Widget db) {
		click(db.$n("btn"));
		waitResponse();

		click(jq(db.$n("pp")).find(".z-calendar-left"));
		waitResponse(true);
		click(jq(db.$n("pp")).find(".z-calendar-cell:contains(11)"));

		// Both a disabled button or an error are okay
		final boolean btnDisabled = jq(db.$n("pp"))
				.find(".z-calendar-cell:contains(11)")
				.hasClass("z-calendar-disabled");
		if (!btnDisabled)
			Assert.assertTrue(hasError());
		else
			click(db.$n("btn")); // close popup

		type(db.$n("real"), "");
		sleep(100);
		Assert.assertFalse(hasError()); // Ensure the previous error is cleaned
	}

	private void testDateRange(Widget db) {
		click(db.$n("btn"));
		waitResponse();

		click(jq(db.$n("pp")).find(".z-calendar-cell:contains(11)"));

		final boolean btnDisabled = jq(db.$n("pp"))
				.find(".z-calendar-cell:contains(11)")
				.hasClass("z-calendar-disabled");
		Assert.assertTrue(btnDisabled);

		click(db.$n("btn")); // close popup
	}

	private void testDateRangeByServer(Widget db) {
		click(db.nextSibling());
		waitResponse();
		Assert.assertTrue(hasError());

		type(db.$n("real"), "");
		sleep(100);
		Assert.assertFalse(hasError());
	}

	private void testTimeRange(Widget tb) {
		final Element inp = tb.$n("real");
		typeTimebox(inp, "000000");
		sleep(100);
		Assert.assertTrue(hasError());

		typeTimebox(inp, "150000");
		Assert.assertFalse(hasError());
	}

	private void typeTimebox(Element inp, String text) {
		click(inp);
		setCursorPosition(inp, 0);
		sendKeys(inp, text);
		click(jq("body"));
	}

	private void testTimeRangeByPick(Widget tp) {
		click(tp.$n("btn"));
		waitResponse();
		click(jq(tp.$n("cave")).find(".z-timepicker-option").first());
		waitResponse();
		Assert.assertTrue(hasError());

		click(tp.$n("btn"));
		waitResponse();
		click(jq(tp.$n("cave")).find(".z-timepicker-option").eq(10));
		waitResponse();
		Assert.assertFalse(hasError());
	}

	private void testTimeBefore(Widget tb) {
		final Element inp = tb.$n("real");
		typeTimebox(inp, "210000");
		sleep(100);
		Assert.assertTrue(hasError());

		typeTimebox(inp, "150000");
		sleep(100);
		Assert.assertFalse(hasError());
	}

	private void testTimeBeforeByPick(Widget tp) {
		click(tp.$n("btn"));
		waitResponse();
		click(jq(tp.$n("cave")).find(".z-timepicker-option").eq(20));
		waitResponse();
		Assert.assertTrue(hasError());

		click(tp.$n("btn"));
		waitResponse();
		click(jq(tp.$n("cave")).find(".z-timepicker-option").eq(10));
		waitResponse();
		Assert.assertFalse(hasError());
	}
}
