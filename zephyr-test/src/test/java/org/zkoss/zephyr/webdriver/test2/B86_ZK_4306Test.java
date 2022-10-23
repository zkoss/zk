package org.zkoss.zephyr.webdriver.test2;

import static org.junit.jupiter.api.Assertions.fail;

import org.junit.jupiter.api.Test;

import org.zkoss.zephyr.webdriver.ZephyrClientMVVMTestCase;

/**
 * @author jameschu
 */
public class B86_ZK_4306Test extends ZephyrClientMVVMTestCase {
	@Test
	public void test() {
		connect();
		sleep(2000);
		try {
			click(jq("@button"));
			waitResponse();
		} catch (Exception e) {
			fail();
		}
		assertNoJSError();
	}
}
