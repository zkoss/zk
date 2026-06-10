/* F104_ZK_4305_DaterangeAllowEmptyTest.java

        Purpose:
                
        Description:
                
        History:
                Fri May 08 15:14:12 CST 2026, Created by peakerlee

Copyright (C) 2026 Potix Corporation. All Rights Reserved.
*/

package org.zkoss.zktest.zats.test2;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.JavascriptExecutor;
import org.zkoss.test.webdriver.WebDriverTestCase;

public class F104_ZK_4305_DaterangeAllowEmptyTest extends WebDriverTestCase {

	@Test
	public void testFourModesRender() {
		connect("/test2/F104-ZK-4305-allow-empty.zul");
		waitResponse();

		assertEquals(4, jq(".z-daterangebox").length(),
				"Four daterangebox instances must be present");
	}

	/**
	 * Clear commits an empty range, which the server accepts only under
	 * {@code allowEmpty="both"} — the restrictive modes (none/begin/end) reject
	 * it. The popup must gate the affordance, rendering Clear only for "both",
	 * so the button never offers an action the server would bounce.
	 */
	@Test
	public void testClearButtonGatedByAllowEmpty() {
		connect("/test2/F104-ZK-4305-allow-empty.zul");
		waitResponse();

		assertFalse(clearRendered("$drNone"), "allowEmpty=none must NOT render Clear");
		assertFalse(clearRendered("$drBegin"), "allowEmpty=begin must NOT render Clear");
		assertFalse(clearRendered("$drEnd"), "allowEmpty=end must NOT render Clear");
		assertTrue(clearRendered("$drBoth"), "allowEmpty=both must render Clear");
	}

	/**
	 * Opens the box's popup (created lazily on first open, so the footer DOM
	 * exists only afterward), reports whether the footer holds a Clear button,
	 * then closes it. Probes the box's own {@code _rangePopup} rather than a
	 * document query because the popup is re-parented to {@code <body>} and all
	 * four boxes coexist on the page.
	 */
	private boolean clearRendered(String boxSel) {
		Object present = ((JavascriptExecutor) driver).executeScript(
				"var box = zk.Widget.$(jq(arguments[0])[0]);"
				+ "if (!box) return 'no-widget';"
				+ "box._openPopup();"
				+ "var popup = box._rangePopup;"
				+ "if (!popup || !popup.$n()) return 'no-popup';"
				+ "var has = popup.$n().querySelector('.z-daterangebox-popup-clear') != null;"
				+ "box._closePopup();"
				+ "return has;",
				boxSel);
		waitResponse();
		assertTrue(present instanceof Boolean,
				"popup probe failed for " + boxSel + " (got: " + present + ")");
		return (Boolean) present;
	}
}
