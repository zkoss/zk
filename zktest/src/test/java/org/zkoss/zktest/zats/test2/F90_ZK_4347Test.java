/* F90_ZK_4347_errorsTest.java

	Purpose:
		
	Description:
		
	History:
		Tue Aug 13 15:41:30 CST 2019, Created by rudyhuang

Copyright (C) 2019 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;

import org.zkoss.zktest.zats.WebDriverTestCase;
import org.zkoss.zktest.zats.ztl.JQuery;
import org.zkoss.zktest.zats.ztl.Widget;

/**
 * @author rudyhuang
 */
public class F90_ZK_4347Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();

		testDateboxes();
		testTimeboxes();
		testCalendars();
		testTimepickers();
		testTimepickerMinMax();
	}

	private void testDateboxes() {
		Assert.assertEquals("2020-01-01-01:00:00", jq("@datebox:eq(0) input").val());
		Assert.assertEquals("2020-01-01-01:00:00", jq("@datebox:eq(1) input").val());
		Assert.assertEquals("2020-01-01-01:00:00", jq("@datebox:eq(2) input").val());
		Assert.assertThat(jq("@datebox:eq(3) input").val(), Matchers.startsWith("2020-01-01")); // Localate no date
		Assert.assertThat(jq("@datebox:eq(4) input").val(), Matchers.endsWith("-01:00:00")); // LocalTime no date
	}

	private void testTimeboxes() {
		Assert.assertEquals("01:00:00", jq("@timebox:eq(0) input").val());
		Assert.assertEquals("01:00:00", jq("@timebox:eq(1) input").val());
		Assert.assertEquals("01:00:00", jq("@timebox:eq(2) input").val());
		Assert.assertEquals("01:00:00", jq("@timebox:eq(3) input").val());
	}

	private void testCalendars() {
		jq("@calendar").iterator().forEachRemaining(c -> {
			final Widget cal = c.toWidget();
			Assert.assertEquals("2020", jq(cal.$n("ty")).text());
			Assert.assertEquals("Jan", jq(cal.$n("tm")).text());
			Assert.assertEquals("1", c.find(".z-calendar-selected").text());
		});
	}

	private void testTimepickers() {
		Assert.assertEquals("01:00:00", jq("@timepicker:eq(0) input").val());
		Assert.assertEquals("01:00:00", jq("@timepicker:eq(1) input").val());
		Assert.assertEquals("01:00:00", jq("@timepicker:eq(2) input").val());
		Assert.assertEquals("01:00:00", jq("@timepicker:eq(3) input").val());
	}

	private void testTimepickerMinMax() {
		final Widget target = widget("@timepicker:last");
		click(target.$n("btn"));
		waitResponse();

		final JQuery items = jq(target.$n("cave")).children();
		Assert.assertEquals("04:00:00", items.first().text());
		Assert.assertEquals("23:00:00", items.last().text());
	}
}
