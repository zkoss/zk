/* F104_ZK_4305_DaterangeBasicTest.java

        Purpose:
                
        Description:
                
        History:
                Fri May 08 15:14:21 CST 2026, Created by peakerlee

Copyright (C) 2026 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.zkoss.test.webdriver.WebDriverTestCase;

public class F104_ZK_4305_DaterangeBasicTest extends WebDriverTestCase {

	/**
	 * The basic Daterangebox renders the two inputs, the separator, and the
	 * calendar button.
	 */
	@Test
	public void testBasicRender() {
		connect("/test2/F104-ZK-4305-basic.zul");
		waitResponse();

		assertTrue(jq(".z-daterangebox").exists(),
				"Daterangebox root element must be present");
		assertEquals(1, jq(".z-daterangebox-begin").length(),
				"Exactly one begin input");
		assertEquals(1, jq(".z-daterangebox-end").length(),
				"Exactly one end input");
		assertEquals(1, jq(".z-daterangebox-separator").length(),
				"Exactly one separator");
		assertEquals(1, jq(".z-daterangebox-button").length(),
				"Exactly one calendar button");
	}

	/**
	 * Clicking the calendar button opens the popup, which contains a panels
	 * container plus the apply / cancel / clear footer.
	 */
	@Test
	public void testButtonOpensPopup() {
		connect("/test2/F104-ZK-4305-basic.zul");
		waitResponse();

		click(jq(".z-daterangebox-button"));
		waitResponse();

		assertTrue(jq(".z-daterangebox-popup").exists(),
				"Popup root must exist after button click");
		assertTrue(jq(".z-daterangebox-popup-panels").exists(),
				"Popup must contain a panels container");
		assertTrue(jq(".z-daterangebox-popup-clear").exists(), "Clear button");
		assertTrue(jq(".z-daterangebox-popup-cancel").exists(), "Cancel button");
		// No Apply button — auto-apply on second pick handles confirm.
		assertFalse(jq(".z-daterangebox-popup-apply").exists(),
				"Apply button is removed; auto-apply replaces it");
	}
}
