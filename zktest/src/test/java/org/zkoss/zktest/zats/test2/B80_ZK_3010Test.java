package org.zkoss.zktest.zats.test2;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.Keys;

import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

public class B80_ZK_3010Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		waitResponse();
		JQuery cb = jq(".z-chosenbox-input");
		focus(cb);
		sendKeys(cb, "ban");
		waitResponse(true);
		JQuery option = jq(".z-chosenbox-option").eq(1);
		assertEquals("banana", option.text());

		click(option);
		waitResponse();
		JQuery itemContent = jq(".z-chosenbox-item-content");
		assertEquals("banana", itemContent.text());

		focus(cb);
		sendKeys(cb, "ban");
		waitResponse(true);
		assertEquals("Create new option 'ban'", jq(".z-chosenbox-empty-creatable span").text());

		sendKeys(cb, Keys.ENTER);
		waitResponse();

		assertEquals(2, itemContent.length());
		assertEquals("ban", itemContent.eq(1).text());
	}
}
