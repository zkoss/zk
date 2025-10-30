/** B80_ZK_3605Test.java.

	Purpose:
		
	Description:
		
	History:
 		Thu Mar 30 16:00:12 CST 2017, Created by christopher

Copyright (C) 2017 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;

/**
 * @author christopher
 *
 */
public class B80_ZK_3605Test extends WebDriverTestCase {
	
	@Test
	public void test() {
		connect();
		// click the 1st row of buttons in order
		click(jq("$btnA1"));
		waitResponse();
		click(jq("$btnB1"));
		waitResponse();
		click(jq("$btnC1"));
		waitResponse();

		// trigger event
		click(jq("$fire"));
		waitResponse();

		// should see the expected result, minus the trimmed new line chars
		String expected = "onAnyWatch, A, p1\nonAnyWatch, B, p2\nonAnyWatch, C, p1";
		assertEquals(expected, getZKLog(), "should contain text from all three listeners");
		closeZKLog();
		waitResponse();

		// click the 2nd row of buttons in order
		click(jq("$btnA2"));
		waitResponse();
		click(jq("$btnB2"));
		waitResponse();
		click(jq("$btnC2"));
		waitResponse();

		// trigger the event
		click(jq("$fire"));
		waitResponse();

		// should not see anything in zklog
		assertEquals(true, !isZKLogAvailable() || getZKLog().length() == 0,
				"should not see any message in zk log");
	}

	@Test
	public void testReverse() {
		connect();
		// click the 1st row of buttons in order
		click(jq("$btnA1"));
		waitResponse();
		click(jq("$btnB1"));
		waitResponse();
		click(jq("$btnC1"));
		waitResponse();

		// trigger event
		click(jq("$fire"));
		waitResponse();

		// should see the expected result, minus the trimmed new line chars
		String expected = "onAnyWatch, A, p1\nonAnyWatch, B, p2\nonAnyWatch, C, p1";
		assertEquals(expected, getZKLog(), "should contain text from all three listeners");
		closeZKLog();
		waitResponse();

		// click the 2nd row of buttons in REVERSE order
		click(jq("$btnC2"));
		waitResponse();
		click(jq("$btnB2"));
		waitResponse();
		click(jq("$btnA2"));
		waitResponse();

		// trigger the event
		click(jq("$fire"));
		waitResponse();

		// should not see anything in zklog
		assertEquals(true, !isZKLogAvailable() || getZKLog().length() == 0,
				"should not see any message in zk log");
	}
}
