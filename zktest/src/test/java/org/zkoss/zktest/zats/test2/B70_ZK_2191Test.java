package org.zkoss.zktest.zats.test2;

import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;

/**
 * @author jameschu
 */
public class B70_ZK_2191Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		waitResponse();
		click(jq("@detail"));
		assertNoJSError();
	}
}
