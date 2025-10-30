/* B80_ZK_2964Test.java

	Purpose:
		
	Description:
		
	History:
		10:33 AM 12/22/15, Created by jumperchen

Copyright (C) 2015 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.DateTimeParseException;
import java.util.Locale;

import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;
import org.zkoss.test.webdriver.ztl.Widget;

/**
 * @author jumperchen
 */
public class B80_ZK_2964Test extends WebDriverTestCase {
	private static final DateTimeFormatter formatter = new DateTimeFormatterBuilder()
			.appendPattern("[h:mm:ss a]")
			.appendPattern("[a h:mm:ss]")
			.appendPattern("[h:mm a]")
			.appendPattern("[a h:mm]")
			.toFormatter(Locale.ENGLISH);

	@Test
	public void testZK2964() throws DateTimeParseException {
		connect();
		JQuery dateboxes = jq("@datebox");
		testTimeformat(widget(dateboxes.get(0)));
		testTimeformat(widget(dateboxes.get(1)));
		testTimeformat(widget(dateboxes.get(2)));
		testTimeformat(widget(dateboxes.get(3)));
	}

	private void testTimeformat(Widget widget)
			throws DateTimeParseException {
		click(widget.$n("btn"));
		assertTrue(jq(widget.$n("pp")).find(".z-timebox-input").exists());
		formatter.parse(jq(widget.$n("pp")).find(".z-timebox-input").val());
	}
}
