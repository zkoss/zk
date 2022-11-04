package org.zkoss.zephyr.webdriver.test2;

import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;

public class B86_ZK_4366Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		sleep(2000);
		assertNoAnyError();
	}
}