package org.zkoss.zktest.zats.test2;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Calendar;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.Keys;

import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

public class B50_3096342Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		click(jq(jq(".z-datebox").toWidget().$n("btn")));
		waitResponse();
		click(jq(".z-timebox").toWidget().$n("btn-down"));
		int index = 19;
		JQuery dayOnCalendar = jq(".z-calendar-weekday:eq(" + index + ")");
		int dayOfMonth = parseInt(dayOnCalendar.text());
		Calendar calendar = Calendar.getInstance();
		if (dayOfMonth == calendar.get(Calendar.DAY_OF_MONTH)) {
			index = dayOfMonth - 1;
			if (dayOfMonth == 1) {
				index = dayOfMonth + 1;
			}
			dayOnCalendar = jq(".z-calendar-weekday:eq(" + index + ")");
		}
		click(dayOnCalendar);
		waitResponse();
		click(jq("$desp"));
		waitResponse();
		String msg = jq("@label:last").text();
		assertTrue(msg.indexOf("d2:onChange:") != -1 && msg.indexOf("null") == -1);
		JQuery dateInput = jq(jq(".z-datebox").toWidget().$n("real"));
		dateInput.toElement().set("value", "");
		sendKeys(dateInput, "" + Keys.TAB);
		waitResponse();
		blur(dateInput);
		msg = jq("@label:last").text();
		assertEquals("d2:onChange: null", jq("@label:last").text());
		assertTrue(msg.indexOf("d2:onChange:") != -1 && msg.indexOf("null") != -1);
	}
}
