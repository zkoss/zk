package org.zkoss.zephyr.webdriver.test2;

import static org.junit.jupiter.api.Assertions.assertNotEquals;

import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;

/**
 * @author jameschu
 */
public class F96_ZK_4761Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		waitResponse();
		click(jq("@button"));
		waitResponse();
		assertNotEquals("0", jq("$resultNum").text());
	}
}