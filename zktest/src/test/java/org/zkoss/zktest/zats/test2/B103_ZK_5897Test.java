/* B103_ZK_5897Test.java

	Purpose:
		Test camera error logging when devices are unavailable

	Description:
		Verifies that onCameraUnavailable event is fired and logged on server
		when getUserMedia fails in headless browser

	History:
		Sun Jan 05 16:23:00 CST 2026, Created by jumperchen

Copyright (C) 2026 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;

/**
 * Test for ZK-5897: Camera should log error on server when devices are unavailable
 *
 * @author jumperchen
 */
public class B103_ZK_5897Test extends WebDriverTestCase {

	@Test
	public void testCameraUnavailableEventFired() {
		connect();

		// Click button to request camera
		click(jq("$btn"));
		waitResponse();

		// In headless browser, getUserMedia will fail with NotAllowedError
		// Verify that onCameraUnavailable event was fired
		assertEquals("Status: onCameraUnavailable fired!", jq("$status").text());

		// Verify error name is set
		// Note: In headless Chrome, this might be generic "Error" instead of specific DOMException names
		String errorName = jq("$errorName").text();
		assertTrue(!errorName.equals("Error Name: (none)"),
			"Expected error name to be set, but got: " + errorName);

		// Verify error message is set
		String errorMessage = jq("$errorMessage").text();
		assertTrue(!errorMessage.equals("Error Message: (none)"),
			"Expected error message to be set, but got: " + errorMessage);
	}
}
