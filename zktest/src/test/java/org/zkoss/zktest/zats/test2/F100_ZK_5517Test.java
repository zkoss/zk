/* F100_ZK_5517Test.java

        Purpose:
                
        Description:
                
        History:
                Fri Dec 08 18:44:37 CST 2023, Created by jamson

Copyright (C) 2023 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.Keys;
import org.openqa.selenium.interactions.Actions;

import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.Widget;

public class F100_ZK_5517Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();

		Actions actions = getActions();
		Widget cld = jq("@calendar").toWidget();
		String month = getMonth(cld);
		int year = getYear(cld),
			date = getDate();

		click(jq(".z-calendar-selected"));
		waitResponse();

		if ("Feb".equals(month) && date == 29) {
			date = 28;
			actions.keyDown(Keys.ARROW_LEFT).perform();
		}

		actions.keyDown(Keys.SHIFT).keyDown(Keys.PAGE_UP).perform();
		waitResponse();

		assertEquals(year, getYear(cld) + 1);
		assertEquals(month, getMonth(cld));
		assertEquals(date, getDate());

		actions.keyDown(Keys.SHIFT).keyDown(Keys.PAGE_DOWN).perform();
		waitResponse();

		actions.keyDown(Keys.SHIFT).keyDown(Keys.PAGE_DOWN).perform();
		waitResponse();

		assertEquals(year, getYear(cld) - 1);
		assertEquals(month, getMonth(cld));
		assertEquals(date, getDate());

		actions.keyDown(Keys.SHIFT).keyDown(Keys.PAGE_UP).perform();
		waitResponse();

		assertEquals(year, getYear(cld));
		assertEquals(month, getMonth(cld));
		assertEquals(date, getDate());
	}

	public int getYear(Widget calendar) {
		return Integer.parseInt(calendar.$n("ty").get("innerHTML"));
	}

	public String getMonth(Widget calendar) {
		return calendar.$n("tm").get("innerHTML");
	}

	public int getDate() {
		return Integer.parseInt(jq(".z-calendar-selected").toElement().get("innerHTML"));
	}
}
