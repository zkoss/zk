package org.zkoss.zktest.zats.test2;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

/**
 * @author bob peng
 */
public class B85_ZK_3578Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		JQuery btnToggle = jq("$toggle");
		click(btnToggle);
		waitResponse();
		assertEquals(1, jq(".z-columns-bar").length());
	}
}