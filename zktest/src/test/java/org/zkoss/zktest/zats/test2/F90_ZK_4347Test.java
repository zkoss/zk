/* F90_ZK_4347_errorsTest.java

	Purpose:
		
	Description:
		
	History:
		Tue Aug 13 15:41:30 CST 2019, Created by rudyhuang

Copyright (C) 2019 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.hamcrest.MatcherAssert.assertThat;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;
import org.zkoss.test.webdriver.ztl.Widget;

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
		Assertions.assertEquals("2020-01-01-01:00:00", jq("@datebox:eq(0) input").val());
		Assertions.assertEquals("2020-01-01-01:00:00", jq("@datebox:eq(1) input").val());
		Assertions.assertEquals("2020-01-01-01:00:00", jq("@datebox:eq(2) input").val());
		assertThat(jq("@datebox:eq(3) input").val(), Matchers.startsWith("2020-01-01")); // Localate no date
		assertThat(jq("@datebox:eq(4) input").val(), Matchers.endsWith("-01:00:00")); // LocalTime no date
	}

	private void testTimeboxes() {
		Assertions.assertEquals("01:00:00", jq("@timebox:eq(0) input").val());
		Assertions.assertEquals("01:00:00", jq("@timebox:eq(1) input").val());
		Assertions.assertEquals("01:00:00", jq("@timebox:eq(2) input").val());
		Assertions.assertEquals("01:00:00", jq("@timebox:eq(3) input").val());
	}

	private void testCalendars() {
		jq("@calendar").iterator().forEachRemaining(c -> {
			final Widget cal = c.toWidget();
			Assertions.assertEquals("2020", jq(cal.$n("ty")).text());
			Assertions.assertEquals("Jan", jq(cal.$n("tm")).text());
			Assertions.assertEquals("1", c.find(".z-calendar-selected").text());
		});
	}

	private void testTimepickers() {
		Assertions.assertEquals("01:00:00", jq("@timepicker:eq(0) input").val());
		Assertions.assertEquals("01:00:00", jq("@timepicker:eq(1) input").val());
		Assertions.assertEquals("01:00:00", jq("@timepicker:eq(2) input").val());
		Assertions.assertEquals("01:00:00", jq("@timepicker:eq(3) input").val());
	}

	private void testTimepickerMinMax() {
		final Widget target = widget("@timepicker:last");
		click(target.$n("btn"));
		waitResponse();

		final JQuery items = jq(target.$n("cave")).children();
		Assertions.assertEquals("04:00:00", items.first().text());
		Assertions.assertEquals("23:00:00", items.last().text());
	}
}
