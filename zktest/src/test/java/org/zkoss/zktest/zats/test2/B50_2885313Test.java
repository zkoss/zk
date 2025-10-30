/* B50_2885313Test.java

	Purpose:
		
	Description:
		
	History:
		Fri Mar 15 16:58:43 CST 2019, Created by rudyhuang

Copyright (C) 2019 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.startsWith;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.Keys;

import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

/**
 * @author rudyhuang
 */
public class B50_2885313Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();

		click(jq("@datebox .z-datebox-button"));
		waitResponse();
		boolean isExactly1200 = LocalTime.now().truncatedTo(ChronoUnit.MINUTES).equals(LocalTime.NOON);
		if (!isExactly1200) { // avoid fail in exact 12:00
			assertThat(jq(".z-datebox-popup @timebox").find("input").val(), not(startsWith("12:00")));
		}

		JQuery input = jq("@datebox input");
		click(input);

		sendKeys(input, Keys.chord(Keys.ALT, Keys.UP));
		waitResponse();
		Assertions.assertFalse(jq(".z-datebox-popup").isVisible());

		sendKeys(input, Keys.chord(Keys.ALT, Keys.DOWN));
		waitResponse();
		Assertions.assertTrue(jq(".z-datebox-popup").isVisible());

		LocalDate currentDate = LocalDate.now();
		sendKeys(input, Keys.chord(Keys.LEFT));
		waitResponse();
		Assertions.assertEquals("" + currentDate.minus(1, ChronoUnit.DAYS).getDayOfMonth(), jq(".z-calendar-selected").text());
		sendKeys(input, Keys.chord(Keys.RIGHT));
		waitResponse();
		Assertions.assertEquals("" + currentDate.getDayOfMonth(), jq(".z-calendar-selected").text());
		sendKeys(input, Keys.chord(Keys.UP));
		waitResponse();
		Assertions.assertEquals("" + currentDate.minus(1, ChronoUnit.WEEKS).getDayOfMonth(), jq(".z-calendar-selected").text());
		sendKeys(input, Keys.chord(Keys.DOWN));
		waitResponse();
		Assertions.assertEquals("" + currentDate.getDayOfMonth(), jq(".z-calendar-selected").text());
	}
}
