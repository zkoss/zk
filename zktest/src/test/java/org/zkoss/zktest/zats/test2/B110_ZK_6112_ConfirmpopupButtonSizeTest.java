/* B110_ZK_6112_ConfirmpopupButtonSizeTest.java

	Purpose:

	Description:

	History:
		Tue Oct 13 2026, Created for ZK-6112 (ZK-6164).

Copyright (C) 2026 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.zkoss.test.webdriver.WebDriverTestCase;

/**
 * Theme-agnostic: whatever the theme, the confirmpopup buttons are as tall as a plain button.
 */
public class B110_ZK_6112_ConfirmpopupButtonSizeTest extends WebDriverTestCase {
	@Test
	public void testButtonsAreAsTallAsAPlainButton() {
		connect();
		waitResponse();
		click(jq("$opener"));
		waitResponse();

		int plainHeight = jq("$plain").outerHeight();
		assertEquals(plainHeight, jq(".z-confirmpopup-ok").outerHeight(), "OK height must match .z-button");
		assertEquals(plainHeight, jq(".z-confirmpopup-cancel").outerHeight(), "Cancel height must match .z-button");
	}
}
