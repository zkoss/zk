/* F00718Test.java
	Purpose:

	Description:

	History:
		Wed Apr 27 15:12:24 CST 2021, Created by jameschu

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.bind.issue;

import org.junit.Test;
import org.openqa.selenium.Keys;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

import static org.junit.Assert.assertEquals;

import org.zkoss.zktest.zats.WebDriverTestCase;
import org.zkoss.zktest.zats.ztl.JQuery;

/**
 * @author jameschu
 */
public class F00718Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		JQuery tb1 = jq("$tb1");
		sendKeys(tb1, Keys.BACK_SPACE, Keys.BACK_SPACE, Keys.BACK_SPACE, Keys.BACK_SPACE, Keys.BACK_SPACE, Keys.BACK_SPACE, Keys.BACK_SPACE, Keys.TAB);
		waitResponse();
		assertEquals(1, jq("@errorbox").length());
		sendKeys(tb1, "a");
		blur(tb1);
		waitResponse();
		assertEquals(0, jq("@errorbox").length());

		JQuery tb2 = jq("$tb2");
		sendKeys(tb2, Keys.BACK_SPACE, Keys.BACK_SPACE, Keys.BACK_SPACE, Keys.BACK_SPACE, Keys.TAB);
		waitResponse();
		assertEquals("Last name can not be null", jq("$msg2").text());
		sendKeys(tb2, "b", Keys.TAB);
		waitResponse();
		assertEquals("", jq("$msg2").text());

		JQuery tb3 = jq("$tb3");
		sendKeys(tb3, "123");
		blur(tb3);
		waitResponse();
		assertEquals("not a well-formed email address", jq("$msg3").text());
		sendKeys(tb3, "@123");
		blur(tb3);
		waitResponse();
		assertEquals("email length must large than 8", jq("$msg3").text());
		sendKeys(tb3, "456");
		blur(tb3);
		waitResponse();
		assertEquals("", jq("$msg3").text());

		JQuery db4 = jq("$db4 .z-datebox-input");
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd").withZone(ZoneId.of("Asia/Taipei"));
		Instant today = Instant.now();
		Instant future = today.plus(2, ChronoUnit.DAYS);
		click(db4);
		sendKeys(db4, Keys.END, Keys.BACK_SPACE, Keys.BACK_SPACE, Keys.BACK_SPACE, Keys.BACK_SPACE, Keys.BACK_SPACE, Keys.BACK_SPACE, Keys.BACK_SPACE, Keys.BACK_SPACE, Keys.BACK_SPACE, Keys.BACK_SPACE);
		waitResponse();
		sendKeys(db4, formatter.format(future));
		blur(db4);
		waitResponse();
		assertEquals("Birth date must be in the past", jq("$msg4").text());
		Instant past = today.minus(2, ChronoUnit.DAYS);
		click(db4);
		sendKeys(db4, Keys.END, Keys.BACK_SPACE, Keys.BACK_SPACE, Keys.BACK_SPACE, Keys.BACK_SPACE, Keys.BACK_SPACE, Keys.BACK_SPACE, Keys.BACK_SPACE, Keys.BACK_SPACE, Keys.BACK_SPACE, Keys.BACK_SPACE);
		waitResponse();
		sendKeys(db4, formatter.format(past));
		blur(db4);
		waitResponse();
		assertEquals("", jq("$msg4").text());
	}
}