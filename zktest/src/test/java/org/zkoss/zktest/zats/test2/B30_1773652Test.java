package org.zkoss.zktest.zats.test2;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;

/**
 * @author jameschu
 */
public class B30_1773652Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		String source = getWebDriver().getPageSource();
		assertTrue(source.toLowerCase().contains("file not found") && source.toLowerCase().contains("abc.zs"));
	}
}