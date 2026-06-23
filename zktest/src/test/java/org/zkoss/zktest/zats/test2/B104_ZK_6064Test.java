/* B104_ZK_6064Test.java

        Purpose:
                
        Description:
                
        History:
                Wed May 06 16:36:05 CST 2026, Created by peakerlee

Copyright (C) 2026 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

public class B104_ZK_6064Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		waitResponse();

		// ZK-6064: the accessible name is applied by the za11y add-on
		// (signature-a11y augment); skip when za11y is not loaded (the NO_A11Y
		// test variant), where the buttons intentionally carry no aria-label.
		if (!Boolean.valueOf(getEval("!!window.za11y")))
			return;

		// The <signature/> widget renders three toolbar <button> elements
		// (undo, save, clear). Each must expose an accessible name so that
		// screen readers and accessibility audits (Lighthouse) can identify
		// them.
		JQuery undo = jq(".z-signature button.z-signature-tool-button:has(.z-signature-tool-button-undo)");
		JQuery save = jq(".z-signature button.z-signature-tool-button:has(.z-signature-tool-button-save)");
		JQuery clear = jq(".z-signature button.z-signature-tool-button:has(.z-signature-tool-button-clear)");

		assertTrue(undo.exists(), "undo button should exist");
		assertTrue(save.exists(), "save button should exist");
		assertTrue(clear.exists(), "clear button should exist");

		assertHasAccessibleName(undo, "undo");
		assertHasAccessibleName(save, "save");
		assertHasAccessibleName(clear, "clear");
	}

	private static void assertHasAccessibleName(JQuery btn, String which) {
		String ariaLabel = btn.attr("aria-label");
		String title = btn.attr("title");
		String name = nonEmpty(ariaLabel) ? ariaLabel : title;
		assertNotNull(name,
				"signature " + which + " button missing accessible name (aria-label/title)");
		assertTrue(nonEmpty(name),
				"signature " + which + " button has empty accessible name");
	}

	private static boolean nonEmpty(String s) {
		return s != null && !s.isEmpty() && !"undefined".equals(s) && !"null".equals(s);
	}
}
