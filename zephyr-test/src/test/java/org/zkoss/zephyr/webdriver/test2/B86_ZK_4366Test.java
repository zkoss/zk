package org.zkoss.zephyr.webdriver.test2;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;

public class B86_ZK_4366Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		waitResponse();
		assertEquals(2, jq("@div").length());
		assertEquals("vm: java.lang.Object", jq("@div").eq(1).find("@label").text().trim());
		assertNoAnyError();
	}
}