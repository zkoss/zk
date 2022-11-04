package org.zkoss.zephyr.webdriver.mvvm.issue;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

public class B01164ImageContentTypeTest extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		JQuery img = jq("$img");
		assertTrue(img.length() > 0);
	}
}
