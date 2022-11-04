package org.zkoss.zephyr.webdriver.mvvm.issue;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;

public class B01167ConverterIdxOutOfBoundTest extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		Assertions.assertTrue(getEval("document.body.innerHTML").contains("org.zkoss.zk.ui.UiException: Cannot find converter:something"));
	}
}
