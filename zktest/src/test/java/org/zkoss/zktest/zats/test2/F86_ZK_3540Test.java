package org.zkoss.zktest.zats.test2;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;

public class F86_ZK_3540Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		sleep(1000);
		Assertions.assertFalse(isZKLogAvailable());
	}
}
