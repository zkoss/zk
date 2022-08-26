package org.zkoss.zktest.zats.test2;

import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;

/**
 * @author jameschu
 */
public class B96_ZK_5134Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		assertNoJSError();
	}
}
