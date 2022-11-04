package org.zkoss.zephyr.webdriver.mvvm.issue;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;

public class B01016NestedFormTest extends WebDriverTestCase {
	@Test
	public void test() {
		try {
			connect();
		} catch (Exception e) {
			assertTrue(e.getCause().toString().contains("UiException: doesn't support to load a nested form"));
		}
	}
}
