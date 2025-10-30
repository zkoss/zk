package org.zkoss.zktest.zats.test2;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

public class B86_ZK_4223Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();

		waitResponse();
		JQuery g = jq("@grid");
		Assertions.assertEquals(g.parent().innerWidth(), g.outerWidth());
	}
}
