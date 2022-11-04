package org.zkoss.zephyr.webdriver.mvvm.issue;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

public class F00771_2Test extends WebDriverTestCase {

	@Test
	public void test() {
		connect("/mvvm/issue/F00771.zul");

		JQuery val1 = jq("$val1");
		JQuery val2 = jq("$val2");
		JQuery val3 = jq("$val3");
		JQuery t11 = jq("$t11");
		JQuery l11 = jq("$l11");
		JQuery l12 = jq("$l12");
		JQuery t21 = jq("$t21");
		JQuery l21 = jq("$l21");
		JQuery l22 = jq("$l22");
		JQuery t31 = jq("$t31");
		JQuery l31 = jq("$l31");
		JQuery l32 = jq("$l32");

		type(t11, "ab");
		waitResponse();
		type(t21, "de");
		waitResponse();
		type(t31, "xy");
		waitResponse();
		assertEquals("", val1.text());
		assertEquals("", val2.text());
		assertEquals("", val3.text());
		assertEquals("value1 must equalsIgnoreCase to abc", l11.text());
		assertEquals("value1 must equalsIgnoreCase to abc - by key", l12.text());
		assertEquals("value2 must equalsIgnoreCase to def", l21.text());
		assertEquals("value1 must equalsIgnoreCase to abc - by key", l22.text());
		assertEquals("value3 must equalsIgnoreCase to xyz", l31.text());
		assertEquals("value3 must equalsIgnoreCase to xyz - by key", l32.text());

		JQuery t41 = jq("$t41");
		JQuery t42 = jq("$t42");
		JQuery t43 = jq("$t43");
		JQuery l41 = jq("$l41");
		JQuery l42 = jq("$l42");
		JQuery l43 = jq("$l43");
		JQuery submit = jq("$submit");

		type(t41, "ab");
		waitResponse();
		type(t42, "de");
		waitResponse();
		type(t43, "xy");
		waitResponse();
		click(submit);
		waitResponse();
		assertEquals("", val1.text());
		assertEquals("", val2.text());
		assertEquals("", val3.text());
		assertEquals("value1 must equalsIgnoreCase to abc - by key", l41.text());
		assertEquals("value2 must equalsIgnoreCase to def - by key", l42.text());
		assertEquals("value3 must equalsIgnoreCase to xyz - by key", l43.text());

		type(t41, "ABC");
		waitResponse();
		click(submit);
		waitResponse();
		assertEquals("", val1.text());
		assertEquals("", val2.text());
		assertEquals("", val3.text());
		assertEquals("", l41.text());
		assertEquals("value2 must equalsIgnoreCase to def - by key", l42.text());
		assertEquals("value3 must equalsIgnoreCase to xyz - by key", l43.text());

		type(t42, "DEF");
		waitResponse();
		click(submit);
		waitResponse();
		assertEquals("", val1.text());
		assertEquals("", val2.text());
		assertEquals("", val3.text());
		assertEquals("", l41.text());
		assertEquals("", l42.text());
		assertEquals("value3 must equalsIgnoreCase to xyz - by key", l43.text());

		type(t43, "XYZ");
		waitResponse();
		click(submit);
		waitResponse();
		assertEquals("ABC", val1.text());
		assertEquals("DEF", val2.text());
		assertEquals("XYZ", val3.text());
		assertEquals("", l41.text());
		assertEquals("", l42.text());
		assertEquals("", l43.text());
		assertEquals("", l11.text());
		assertEquals("", l12.text());
		assertEquals("", l21.text());
		assertEquals("", l22.text());
		assertEquals("", l31.text());
		assertEquals("", l32.text());
	}
}
