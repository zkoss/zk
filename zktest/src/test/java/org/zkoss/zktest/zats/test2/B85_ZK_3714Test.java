package org.zkoss.zktest.zats.test2;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNotSame;

import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;



/**
 * @author bob peng
 */
public class B85_ZK_3714Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		click(jq(".z-button:eq(0)"));
		waitResponse();
		click(jq(".z-button:eq(1)"));
		waitResponse();
		String bugResult = jq(".z-label:eq(1)").text();
		assertEquals(bugResult, jq(".z-label:eq(2)").text());
		assertNotSame("null", bugResult);
		assertNotNull(bugResult);
	}
}