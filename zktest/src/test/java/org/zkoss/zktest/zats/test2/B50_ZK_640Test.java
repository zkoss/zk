package org.zkoss.zktest.zats.test2;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

public class B50_ZK_640Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		JQuery tbbOne = jq("$tbbOne");
		JQuery tbbTwo = jq("$tbbTwo");

		assertFalse(tbbOne.html().contains("&nbsp;"), "no extra space if image only");
		assertTrue(tbbTwo.html().contains("&nbsp;"), "space exists between label and image");
	}
}
