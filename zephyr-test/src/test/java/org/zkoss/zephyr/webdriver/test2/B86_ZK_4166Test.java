package org.zkoss.zephyr.webdriver.test2;

import static org.junit.jupiter.api.Assertions.assertFalse;

import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;

public class B86_ZK_4166Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		assertFalse(isZKLogAvailable());
		sleep(1000);
		assertFalse(isZKLogAvailable());
	}
}
