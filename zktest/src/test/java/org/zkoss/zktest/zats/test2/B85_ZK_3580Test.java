package org.zkoss.zktest.zats.test2;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;



/**
 * @author bob peng
 */
public class B85_ZK_3580Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		assertEquals("SUCCESS!!!", jq(".z-div span:eq(0)").text(), "Should see SUCCESS!!!");
	}
}