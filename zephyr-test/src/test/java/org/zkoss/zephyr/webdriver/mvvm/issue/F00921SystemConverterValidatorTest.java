package org.zkoss.zephyr.webdriver.mvvm.issue;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

public class F00921SystemConverterValidatorTest extends WebDriverTestCase {

	@Test
	public void test() {
		connect();

		JQuery l1 = jq("$l1");
		JQuery l2 = jq("$l2");
		JQuery l3 = jq("$l3");
		JQuery l4 = jq("$l4");

		assertEquals("XConverterX", l1.text());
		assertEquals("YConverterY", l2.text());
		assertEquals("XValidator", l3.text());
		assertEquals("YValidator", l4.text());
	}
}
