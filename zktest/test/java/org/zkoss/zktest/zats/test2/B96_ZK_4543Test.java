/* B96_ZK_4543Test.java

	Purpose:

	Description:

	History:
		Thu Jul 01 18:01:16 CST 2021, Created by katherine

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.Keys;
import org.openqa.selenium.interactions.Actions;
import org.zkoss.zktest.zats.WebDriverTestCase;
import org.zkoss.zktest.zats.ztl.JQuery;

import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.Locale;


public class B96_ZK_4543Test extends WebDriverTestCase {
	@Test
	public void testNoPast() {
		connect();
		JQuery c1 = jq("$c1");
		JQuery cell = c1.find(".z-calendar-cell:eq(10)");
		JQuery rightBtn = c1.find(".z-calendar-right");
		JQuery leftBtn = c1.find(".z-calendar-left");
		String lastMonth = LocalDate.now().minusMonths(1).getMonth().getDisplayName(TextStyle.SHORT, Locale.ENGLISH);

		//go to next month
		click(rightBtn);
		waitResponse();

		//go to previous month
		click(cell);
		click(leftBtn);
		waitResponse();
		click(leftBtn);
		waitResponse();
		click(jq("@button:contains(c1)"));
		waitResponse();
		Assert.assertFalse(c1.find(".z-calendar-selected").exists());
		Assert.assertFalse(jq("$lb1").text().contains(lastMonth));

		//go to next month
		click(rightBtn);
		waitResponse();
		click(rightBtn);
		waitResponse();
		Assert.assertTrue(cell.hasClass("z-calendar-selected"));
	}
	@Test
	public void testBetween() {
		connect();
		JQuery c2 = jq("$c2");
		JQuery rightBtn = c2.find(".z-calendar-right");
		Actions action = getActions();
		click(jq("@button:contains(to 2021/08/02)"));
		waitResponse();

		// go to 202109
		click(rightBtn);
		waitResponse();
		click(c2.find(".z-calendar-cell:eq(3)"));
		action.sendKeys(Keys.ARROW_RIGHT).perform();
		click(jq("@button:contains(c2)"));
		waitResponse();
		Assert.assertTrue(jq("$lb2").text().contains("Sep 01"));

		// go to 202110
		click(rightBtn);
		waitResponse();
		Assert.assertFalse(c2.find(".z-calendar-selected").exists());
	}
	@Test
	public void testNoFuture() {
		connect();
		JQuery c3 = jq("$c3");
		JQuery cell = c3.find(".z-calendar-cell:eq(10)");
		JQuery rightBtn = c3.find(".z-calendar-right");
		JQuery leftBtn = c3.find(".z-calendar-left");
		String nextMonth = LocalDate.now().plusMonths(1).getMonth().getDisplayName(TextStyle.SHORT, Locale.ENGLISH);

		//go to previous month
		click(leftBtn);
		waitResponse();
		click(cell);

		//go to next month
		click(rightBtn);
		waitResponse();
		click(rightBtn);
		waitResponse();
		Assert.assertFalse(c3.find(".z-calendar-selected").exists());
		click(jq("@button:contains(c3)"));
		waitResponse();
		Assert.assertFalse(jq("$lb3").text().contains(nextMonth));

		//go to previous month
		click(leftBtn);
		waitResponse();
		click(leftBtn);
		waitResponse();
		Assert.assertTrue(cell.hasClass("z-calendar-selected"));
	}
	@Test
	public void testNoToday() {
		connect();
		JQuery c4 = jq("@calendar:eq(4)");
		Assert.assertFalse(c4.find(".z-calendar-selected").exists());
	}
}
