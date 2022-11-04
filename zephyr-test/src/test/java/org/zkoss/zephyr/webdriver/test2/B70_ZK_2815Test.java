package org.zkoss.zephyr.webdriver.test2;

import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;

public class B70_ZK_2815Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		click(jq("@checkbox").eq(2));
		waitResponse();
		assertNoAnyError();
	}
}
