package org.zkoss.zephyr.webdriver.mvvm.issue;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;

public class B01196BinderUiLcConfusionTest extends WebDriverTestCase {
	@Test
	public void test() {
		connect();

		assertEquals(5, jq("@listitem").length());
	}
}
