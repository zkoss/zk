package org.zkoss.zephyr.webdriver.test2;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;

/**
 * @author jameschu
 */
public class F80_ZK_2675Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		testClick();
	}

	@Test
	public void test1() {
		connect("/test2/F80-ZK-2675-1.zul");
		testClick();
	}

	private void testClick() {
		click(jq("@button").eq(0));
		waitResponse();
		assertEquals("clicked", getZKLog());
		closeZKLog();
		click(jq("@button").eq(1));
		click(jq("@button").eq(2));
		waitResponse();
		assertEquals("clicked 2", getZKLog());
		closeZKLog();
		click(jq("@button").eq(3));
		waitResponse();
		assertEquals("clicked 3", getZKLog());
	}
}