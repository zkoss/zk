package org.zkoss.zktest.zats.test2;

import static org.junit.jupiter.api.Assertions.assertFalse;

import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;

public class B30_1857731Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		click(jq("@toolbarbutton"));
		waitResponse();
		assertFalse(hasError());
	}
}