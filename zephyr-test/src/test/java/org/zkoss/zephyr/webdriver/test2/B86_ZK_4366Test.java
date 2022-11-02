package org.zkoss.zephyr.webdriver.test2;

import org.junit.jupiter.api.Test;

import org.zkoss.zephyr.webdriver.ClientBindTestCase;

public class B86_ZK_4366Test extends ClientBindTestCase {
	@Test
	public void test() {
		connect();
		sleep(2000);
		assertNoAnyError();
	}
}