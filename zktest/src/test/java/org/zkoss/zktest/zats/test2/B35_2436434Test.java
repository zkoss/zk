package org.zkoss.zktest.zats.test2;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;

public class B35_2436434Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		click(jq("@button"));
		waitResponse();
		int x = jq("$tabs").scrollLeft();
		click(jq(".z-tabbox").toWidget().$n("right"));
		waitResponse();
		int y = jq("$tabs").scrollLeft();
		assertTrue(x == 0);
		assertTrue(y > 0);
    }
}
