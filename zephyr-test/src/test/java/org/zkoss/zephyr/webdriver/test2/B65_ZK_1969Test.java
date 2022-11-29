package org.zkoss.zephyr.webdriver.test2;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.Keys;

import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

/**
 * @author jameschu
 */
public class B65_ZK_1969Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		JQuery button = jq("@button");
		click(button);
		waitResponse();
		click(jq("@listitem").eq(19));
		waitResponse();
		assertEquals("1", jq(".z-paging-input").val());
		getActions().sendKeys(Keys.ARROW_DOWN).perform();
		waitResponse();
		assertEquals("2", jq(".z-paging-input").val());
		assertTrue(jq("@listitem").eq(0).hasClass("z-listitem-selected"));
		assertNoAnyError();
	}
}