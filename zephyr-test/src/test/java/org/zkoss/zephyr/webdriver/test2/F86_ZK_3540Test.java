package org.zkoss.zephyr.webdriver.test2;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import org.zkoss.zephyr.webdriver.ClientBindTestCase;

public class F86_ZK_3540Test extends ClientBindTestCase {
	@Test
	public void test() {
		connect();
		sleep(1000);
		Assertions.assertFalse(isZKLogAvailable());
	}
}
