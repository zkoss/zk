package org.zkoss.clientbind.webdriver.test2;

import static org.junit.jupiter.api.Assertions.assertFalse;

import org.junit.jupiter.api.Test;

import org.zkoss.clientbind.webdriver.ClientBindTestCase;

public class B86_ZK_4166Test extends ClientBindTestCase {
	@Test
	public void test() {
		connect();
		assertFalse(isZKLogAvailable());
		sleep(1000);
		assertFalse(isZKLogAvailable());
	}
}
