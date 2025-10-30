package org.zkoss.zktest.zats.test2;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;

/**
 * @author bob peng
 */
public class B85_ZK_3726Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		click(jq(".z-datebox-icon.z-icon-calendar:eq(0)"));
		waitResponse();
		click(jq("body"));
		waitResponse();
		click(jq(".z-datebox-icon.z-icon-calendar:eq(1)"));
		waitResponse();
		click(jq("body"));
		waitResponse();

		assertEquals("1\n52", getZKLog(), "The week number of 2012/01/01 in the first datebox should be 1.\n" +
				"The week number of 2012/01/01 in the second datebox should be 52.");
	}
}