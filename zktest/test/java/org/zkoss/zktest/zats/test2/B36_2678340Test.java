package org.zkoss.zktest.zats.test2;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Test;

import org.openqa.selenium.Keys;
import org.zkoss.zktest.zats.WebDriverTestCase;
import org.zkoss.zktest.zats.ztl.JQuery;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * @author jameschu
 */
public class B36_2678340Test extends WebDriverTestCase {
	@Test public void test() {
		connect();
		// Click upper button twice
		click(jq(".z-timebox").toWidget().$n("btn-down"));
		waitResponse();
		click(jq(".z-timebox").toWidget().$n("btn-down"));
		waitResponse();

		JQuery tb = jq("$tb1");
		// Value of the label
		String v = tb.find("input").val();
		// Location of time separator ':'
		int p = v.indexOf(":");
		// Construct a parseable time format
		String value1 = v.substring(p - 2, p + 6);
		// value of the timebox
		String r = jq(tb.toWidget().$n("real")).val();
		p = r.indexOf(":");
		String value2 = r.substring(p - 2, p + 6);

		DateFormat df1 = new SimpleDateFormat("hh:mm:ss");
		Calendar calendar = Calendar.getInstance();
		try {
			calendar.setTime(df1.parse(value1));
		} catch (ParseException e) {
			e.printStackTrace();
			fail();
		}

		int hh1 = calendar.get(Calendar.HOUR_OF_DAY);
		int mm1 = calendar.get(Calendar.MINUTE);
		int ss1 = calendar.get(Calendar.SECOND);

		try {
			calendar.setTime(df1.parse(value2));
		} catch (ParseException e) {
			e.printStackTrace();
			fail();
		}
		int hh2 = calendar.get(Calendar.HOUR_OF_DAY);
		int mm2 = calendar.get(Calendar.MINUTE);
		int ss2 = calendar.get(Calendar.SECOND);

		assertTrue("The time value should of the label should be equal to the timebox", hh1 == hh2 && mm1 == mm2 && ss1 == ss2);

		// Click upper button again
		click(jq(".z-timebox").toWidget().$n("btn-up"));
		sendKeys(tb.toWidget().$n("real"), Keys.TAB);

		// Click on show value button again
		click(jq("@button"));
		waitResponse();

		try {
			calendar.setTime(df1.parse(value1));
		} catch (ParseException e) {
			e.printStackTrace();
			fail();
		}

		hh1 = calendar.get(Calendar.HOUR_OF_DAY);
		mm1 = calendar.get(Calendar.MINUTE);
		ss1 = calendar.get(Calendar.SECOND);

		try {
			calendar.setTime(df1.parse(value2));
		} catch (ParseException e) {
			e.printStackTrace();
		}
		hh2 = calendar.get(Calendar.HOUR_OF_DAY);
		mm2 = calendar.get(Calendar.MINUTE);
		ss2 = calendar.get(Calendar.SECOND);

		assertTrue("The time value should of the label should be equal to the timebox", hh1 == hh2 && mm1 == mm2 && ss1 == ss2);
	}
}