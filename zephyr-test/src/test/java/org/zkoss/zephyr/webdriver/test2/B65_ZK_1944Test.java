package org.zkoss.zephyr.webdriver.test2;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import org.zkoss.zephyr.webdriver.ClientBindTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

/**
 * @author jameschu
 */
public class B65_ZK_1944Test extends ClientBindTestCase {
	@Test
	public void test() {
		connect();
		sleep(2000);
		JQuery d1 = jq("$d1");
		JQuery d2 = jq("$d2");
		assertEquals("12.3", d1.val());
		assertEquals("12.3", d2.val());
	}
}