package org.zkoss.zephyr.webdriver.test2;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.Keys;

import org.zkoss.test.webdriver.WebDriverTestCase;

public class B80_ZK_3237Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		sendKeys(jq(".z-combobox-input"), "a");
		waitResponse();
		sendKeys(jq(".z-combobox-input"), Keys.TAB);
		sendKeys(jq(".z-textbox"), "aa");
		waitResponse();
		assertEquals("aa", jq(".z-textbox").val());
	}
}
