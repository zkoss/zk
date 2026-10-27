/* B110_ZK_6112_AnchorDirToggleTest.java

	Purpose:

	Description:

	History:
		Tue Oct 27 2026, Created for ZK-6112.

Copyright (C) 2026 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.zkoss.test.webdriver.WebDriverTestCase;

/**
 * Marble: the icon gap of an anchor depends on the z-a-reverse class, so changing
 * dir on a rendered anchor must add and remove it (ZK-6112).
 */
public class B110_ZK_6112_AnchorDirToggleTest extends WebDriverTestCase {
	@Test
	public void testDirToggleUpdatesReverseClass() {
		connect();
		waitResponse();
		assertFalse(jq("$anchor").hasClass("z-a-reverse"));
		click(jq("$toReverse"));
		waitResponse();
		assertTrue(jq("$anchor").hasClass("z-a-reverse"));
		click(jq("$toNormal"));
		waitResponse();
		assertFalse(jq("$anchor").hasClass("z-a-reverse"));
	}
}
