/* DatetimeboxTest.java

	Purpose:
		
	Description:
		
	History:
		Tue Apr 27 11:38:22 CST 2021, Created by rudyhuang

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zephyr.webdriver.mvvm.book.comp;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.Keys;

import org.zkoss.zephyr.webdriver.ClientBindTestCase;

/**
 * @author rudyhuang
 */
public class DatetimeboxTest extends ClientBindTestCase {
	@Test
	public void test() {
		connect();
		final SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm:ss a");
		final SimpleDateFormat timeboxFormat = new SimpleDateFormat("a hh:mm:ss");
		final SimpleDateFormat dateboxFormat = new SimpleDateFormat("yyyy/MM/dd a hh:mm:ss");
		sendKeys(jq("@timebox input"), Keys.HOME, Keys.ARROW_UP, Keys.ARROW_RIGHT, Keys.ARROW_RIGHT, "111111", Keys.TAB);
		waitResponse();
		Date date = null;
		try {
			date = dateboxFormat.parse(jq(".z-datebox-input").val());
		} catch (ParseException e) {
			fail("Unable to parse date, " + e);
		}
		assertEquals("11:11:11 AM", timeFormat.format(date));
		type(jq("@datebox input"), "2021/01/23 PM 06:05:04");
		waitResponse();
		try {
			date = timeboxFormat.parse(jq(".z-timebox-input").val());
		} catch (ParseException e) {
			fail("Unable to parse date, " + e);
		}
		assertEquals("06:05:04 PM", timeFormat.format(date));
	}
}
