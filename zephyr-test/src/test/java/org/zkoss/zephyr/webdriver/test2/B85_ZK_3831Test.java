package org.zkoss.zephyr.webdriver.test2;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;

public class B85_ZK_3831Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		assertEquals("someValue", jq("$innerLabel").text());
		assertEquals("This is arg1", jq("$label1").text().trim());
		assertEquals("This is arg2", jq("$label2").text());
	}
}