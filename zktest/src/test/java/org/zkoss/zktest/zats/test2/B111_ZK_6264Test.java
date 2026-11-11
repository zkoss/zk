/* B111_ZK_6264Test.java

	Purpose:

	Description:

	History:
		Tue Nov 10 14:11:20 CST 2026, Created by peggypeng

Copyright (C) 2026 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.Keys;

import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

public class B111_ZK_6264Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();

		JQuery timebox = jq("@timebox").eq(0);
		typeAndBlur(timebox, "0", "00:00");
		String log = getZKLog();
		Assertions.assertTrue(log.length() >= 10, "precondition: the blur logs the new value");
		String date = log.substring(0, 10);
		closeZKLog();
		clickUp(timebox, "00:01");
		clickUp(timebox, "00:02");
		click(jq("body"));
		waitResponse();
		Assertions.assertEquals(date + " 00:02:00", getZKLog());

		JQuery input = timebox.find("input");
		click(input);
		setCursorPosition(input, 2);
		getActions().sendKeys(Keys.BACK_SPACE).perform();
		waitResponse();
		Assertions.assertEquals("0 :02", input.val(), "precondition: Backspace pads the hour after its digit");
		click(jq("body"));
		waitResponse();
		Assertions.assertEquals("00:02", input.val());

		typeAndBlur(timebox, "1", "01:00");
		clickUp(timebox, "01:01");
		typeAndBlur(timebox, "0", "00:00");
		clickDown(timebox, "00:59");
		clickDown(timebox, "00:58");

		timebox = jq("@timebox").eq(1);
		typeAndBlur(timebox, "0", "AM 12:00");
		clickUp(timebox, "AM 12:01");
		clickUp(timebox, "AM 12:02");

		timebox = jq("@timebox").eq(2);
		typeAndBlur(timebox, "0", "12:00:00 AM");
		clickUp(timebox, "12:00:00 PM");
		clickUp(timebox, "12:00:00 AM");

		timebox = jq("@timebox").eq(3);
		typeAndBlur(timebox, "0", "0000");
		clickUp(timebox, "0001");
		clickUp(timebox, "0002");

		timebox = jq("@timebox").eq(4);
		typeAndBlur(timebox, "0", "0 :  ");
		Assertions.assertTrue(jq(".z-errorbox").exists(), "precondition: 00:00 fails the constraint");
		clickUp(timebox, "01:  ");
	}

	private void typeAndBlur(JQuery timebox, String keys, String expected) {
		click(timebox.find("input"));
		selectAll();
		getActions().sendKeys(keys).perform();
		click(jq("body"));
		waitResponse();
		Assertions.assertEquals(expected, timebox.find("input").val());
	}

	private void clickUp(JQuery timebox, String expected) {
		click(widget(timebox).$n("btn-up"));
		waitResponse();
		Assertions.assertEquals(expected, timebox.find("input").val());
	}

	private void clickDown(JQuery timebox, String expected) {
		click(widget(timebox).$n("btn-down"));
		waitResponse();
		Assertions.assertEquals(expected, timebox.find("input").val());
	}
}
