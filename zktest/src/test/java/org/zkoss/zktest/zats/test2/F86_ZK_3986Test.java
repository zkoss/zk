package org.zkoss.zktest.zats.test2;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

public class F86_ZK_3986Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		JQuery chosenbox = jq("@chosenbox");
		JQuery chosenboxPopup = jq(".z-chosenbox-select");
		click(chosenbox);
		sleep(1000);
		assertTrue(chosenboxPopup.isVisible());
		sleep(3000);
		click(jq(".z-chosenbox-option:contains(item1)"));
		sleep(1000);
		click(chosenbox);
		sleep(2000);
		click(jq(".z-chosenbox-option:contains(item3)"));
		sleep(1000);
		click(chosenbox);
		for (int i = 0; i < 10; i++) {
			sleep(1000);
			assertTrue(chosenboxPopup.isVisible());
		}
	}
}
