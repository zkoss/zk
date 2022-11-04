package org.zkoss.zephyr.webdriver.test2;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;

public class B65_ZK_1913Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		click(jq(".z-button"));
		waitResponse();
		assertTrue(jq(".z-label:contains(true)").length() == 2, "it will show true.");
	}
}