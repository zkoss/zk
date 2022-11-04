package org.zkoss.zephyr.webdriver.mvvm.issue;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

public class B01062NullIntValueTest extends WebDriverTestCase {
	@Test
	public void test() {
		connect();

		JQuery lb11 = jq("$lb11");
		JQuery lb12 = jq("$lb12");
		JQuery lb21 = jq("$lb21");
		JQuery lb22 = jq("$lb22");
		JQuery msg1 = jq("$msg1");
		JQuery msg2 = jq("$msg2");
		JQuery save = jq("$save");

		assertEquals("", lb11.text());
		assertEquals("0", lb12.text());
		assertEquals("", lb21.text());
		assertEquals("0", lb22.text());

		click(save);
		waitResponse();
		assertEquals("", lb11.text());
		assertEquals("0", lb12.text());
		assertEquals("", lb21.text());
		assertEquals("0", lb22.text());
		assertEquals("value1 is null, value2 is 0", msg1.text());
		assertEquals("value1 is null, value2 is 0", msg2.text());
	}
}
