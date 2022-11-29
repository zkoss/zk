package org.zkoss.zktest.zats.test2;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

public class B70_ZK_2463Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		JQuery addBtn = jq(".z-button:contains(add)");
		click(addBtn);
		waitResponse();
		JQuery col9 = jq(".z-column-content:contains(col 9)");
		assertTrue(col9.length() > 0);
		assertTrue(col9.isVisible());
		assertTrue(col9.width() > 10);
		assertNoAnyError();
	}
}
