package org.zkoss.zephyr.webdriver.test2;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
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
		for (int i = 0; i <= 4; i++) {
			click(jq(".z-datebox:eq(" + i + ")").toWidget().$n("btn"));
			waitResponse();
			click(jq(".z-calendar:eq(" + i + ") .z-calendar-cell:contains(14)"));
			waitResponse();
		}
		assertFalse(jq(".z-errorbox").exists(), "should not see any error message.");
	}
}