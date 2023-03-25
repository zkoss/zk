package org.zkoss.zephyr.webdriver.test2;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

public class B80_ZK_2906Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		JQuery cb = jq(".z-chosenbox-input");
		// click on chosenbox to focus
		click(cb);
		waitResponse(true);

		// type the special characters that will be escaped
		focus(cb);
		sendKeys(cb, "'`&<>\"");
		waitResponse(true);

		String itemText = jq(".z-chosenbox-empty span").text();
		// check the content of the first item is '`&<>"'
		assertEquals("Create new contact ''`&<>\"'", itemText, "expecting '`&<>\", got: " + itemText);
	}
}
