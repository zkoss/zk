package org.zkoss.zephyr.webdriver.test2;

import static org.junit.jupiter.api.Assertions.fail;

import org.junit.jupiter.api.Test;

import org.zkoss.zephyr.webdriver.ClientBindTestCase;

public class B80_ZK_3562Test extends ClientBindTestCase {
	@Test
	public void test() {
		try {
			connect();
			click(jq("$btn"));
			waitResponse();
		} catch (Exception e) {
			fail(e.getMessage());
		}
		assertNoJSError();
	}
}
