/* B110_ZK_6064Test.java

        Purpose:
                
        Description:
                
        History:
                Wed May 06 16:36:05 CST 2026, Created by peakerlee

Copyright (C) 2026 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

public class B110_ZK_6064Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		waitResponse();

		// the NO_A11Y variant intentionally renders no aria-label
		if (!Boolean.parseBoolean(getEval("!!window.za11y")))
			return;

		assertEquals("Undo", button("undo").attr("aria-label"));
		assertEquals("Save", button("save").attr("aria-label"));
		assertEquals("Clear", button("clear").attr("aria-label"));

		click(jq("$setLabel"));
		waitResponse();
		assertEquals("Back", button("undo").text());
		assertEquals("Back", button("undo").attr("aria-label"));

		click(jq("$clearLabel"));
		waitResponse();
		assertEquals("Undo", button("undo").attr("aria-label"));
	}

	private JQuery button(String which) {
		return jq(".z-signature-tool-button:has(.z-signature-tool-button-" + which + ")");
	}
}
