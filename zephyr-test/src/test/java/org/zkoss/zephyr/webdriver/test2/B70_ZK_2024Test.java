package org.zkoss.zephyr.webdriver.test2;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.Keys;

import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

public class B70_ZK_2024Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		sleep(2000);
		JQuery inp = jq(".z-combobox-input");
		sendKeys(inp, "se" + Keys.TAB);
		waitResponse();

		assertEquals(inp.val(), "SE", "the value should show 'SE'.");
		assertTrue(jq(".z-label:contains(Sverige)").exists(), "the label should show 'Sverige'");
	}
}