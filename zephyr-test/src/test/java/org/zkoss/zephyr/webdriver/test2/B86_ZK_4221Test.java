package org.zkoss.zephyr.webdriver.test2;

import static org.junit.jupiter.api.Assertions.fail;

import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

/**
 * @author jameschu
 */
public class B86_ZK_4221Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		waitResponse();
		JQuery cb = jq("@checkbox");
		try {
			click(cb);
			waitResponse();
			click(cb);
			waitResponse();
		} catch (Exception e) {
			fail();
		}
		assertNoAnyError();
	}
}
