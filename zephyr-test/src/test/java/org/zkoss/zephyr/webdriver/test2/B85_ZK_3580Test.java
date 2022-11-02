package org.zkoss.zephyr.webdriver.test2;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import org.zkoss.zephyr.webdriver.ClientBindTestCase;



/**
 * @author bob peng
 */
public class B85_ZK_3580Test extends ClientBindTestCase {
	@Test
	public void test() {
		connect();
		assertEquals("SUCCESS!!!", jq(".z-div span:eq(0)").text(), "Should see SUCCESS!!!");
	}
}