/* B86_ZK_4242Test.java

	Purpose:

	Description:

	History:
		Tue May 21 14:27:54 CST 2019, Created by rudyhuang

Copyright (C) 2019 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import java.time.chrono.ChronoLocalDate;
import java.time.chrono.ThaiBuddhistChronology;
import java.time.temporal.ChronoField;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;

/**
 * @author rudyhuang
 */
public class B86_ZK_4242Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();

		ChronoLocalDate dateNow = ThaiBuddhistChronology.INSTANCE.dateNow();
		String buddhistYear = String.valueOf(dateNow.get(ChronoField.YEAR));

		click(widget("@datebox:first").$n("btn"));
		waitResponse();
		String calendarYear = toElement(widget("@calendar:visible").$n("ty")).getText();
		Assertions.assertEquals(buddhistYear, calendarYear);

		click(widget("@datebox:eq(1)").$n("btn"));
		waitResponse();
		String calendarYear2 = toElement(widget("@calendar:visible").$n("ty")).getText();
		Assertions.assertEquals(buddhistYear, calendarYear2);
	}
}
