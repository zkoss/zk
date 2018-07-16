package org.zkoss.zktest.zats.test2;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import org.zkoss.zktest.zats.WebDriverTestCase;

/**
 * @author jameschu
 */
public class B30_1773652Test extends WebDriverTestCase {
	@Test public void test() {
		connect();
		String source = getWebDriver().getPageSource();
		assertTrue(source.toLowerCase().contains("file not found") && source.toLowerCase().contains("abc.zs"));
	}
}