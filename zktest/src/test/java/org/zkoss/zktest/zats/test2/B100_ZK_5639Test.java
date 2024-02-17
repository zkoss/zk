/* B100_ZK_5639Test.java

	Purpose:
		
	Description:
		
	History:
		11:57 AM 2024/2/17, Created by jumperchen

Copyright (C) 2024 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;

/**
 * @author jumperchen
 */
public class B100_ZK_5639Test extends WebDriverTestCase {
	@Test
	public void testCase1() {
		connect();
		click(jq(".stepbar-button:contains(Yes, I need a Car!)"));
		waitResponse();
		assertEquals(6, jq(".stepbar .step").length());

		click(jq(".stepbar-button:contains(Next)"));
		waitResponse();

		assertTrue(jq(".stepbar .step").eq(0).hasClass("previous"));
		assertTrue(jq(".stepbar .step").eq(1).hasClass("current"));

		click(jq(".stepbar-button:contains(Next)"));
		waitResponse();

		assertTrue(jq(".stepbar .step").eq(0).hasClass("previous"));
		assertTrue(jq(".stepbar .step").eq(1).hasClass("previous"));
		assertTrue(jq(".stepbar .step").eq(2).hasClass("current"));

		click(jq(".stepbar-button:contains(Next)"));
		waitResponse();

		assertTrue(jq(".stepbar .step").eq(0).hasClass("previous"));
		assertTrue(jq(".stepbar .step").eq(1).hasClass("previous"));
		assertTrue(jq(".stepbar .step").eq(2).hasClass("previous"));
		assertTrue(jq(".stepbar .step").eq(3).hasClass("current"));
	}

	@Test
	public void testCase2() {
		connect();
		click(jq(".stepbar-button:contains(Yes, I need a Car!)"));
		waitResponse();

		click(jq(".stepbar-button:contains(Next)"));
		waitResponse();

		click(jq(".stepbar-button:contains(Next)"));
		waitResponse();

		click(jq(".stepbar-button:contains(Remove Car)"));
		waitResponse();

		click(jq(".stepbar-button:contains(Next)"));
		waitResponse();

		assertTrue(jq(".stepbar .step").eq(0).hasClass("previous"));
		assertTrue(jq(".stepbar .step").eq(1).hasClass("previous"));
		assertTrue(jq(".stepbar .step").eq(2).hasClass("previous"));
		assertTrue(jq(".stepbar .step").eq(3).hasClass("current"));
	}

	@Test
	public void testCase3() {
		testCase2();

		for (int i = 0; i < 3; i++) {
			click(jq(".stepbar-button:contains(Back)"));
			waitResponse();
		}
		assertTrue(jq(".stepbar .step").eq(0).hasClass("current"));
		assertFalse(jq(".stepbar .step").eq(1).hasClass("previous"));
		assertFalse(jq(".stepbar .step").eq(2).hasClass("previous"));
		assertFalse(jq(".stepbar .step").eq(3).hasClass("previous"));
	}
}
