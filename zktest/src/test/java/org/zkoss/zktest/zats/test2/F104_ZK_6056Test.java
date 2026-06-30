/* F104_ZK_6056Test.java

        Purpose:
                
        Description:
                
        History:
                Thu Apr 30 11:09:45 CST 2026, Created by peakerlee

Copyright (C) 2026 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

public class F104_ZK_6056Test extends WebDriverTestCase {
	@Test
	public void testFilterLogicContainsAttribute() {
		connect("/test2/F104-ZK-6056.zul");
		waitResponse();
		JQuery cb = jq("@chosenbox").eq(0);
		JQuery cbInput = cb.find(".z-chosenbox-input");

		click(cb);
		waitResponse();

		sendKeys(cbInput, "US");
		// Chosenbox debounces onSearching by 350ms
		sleep(500);
		waitResponse();

		JQuery options = jq(".z-chosenbox-select").find(".z-chosenbox-option");
		long visible = countVisible(options);
		// Without the fix, the SubModel's default STRING_COMPARATOR pre-filters
		// by startsWith server-side, and no Locale.toString() starts with "US",
		// so zero options would be visible. With the fix, filterLogic="contains"
		// (set here via custom-attributes) bypasses the SubModel pre-filter and
		// matches locales whose toString contains "US" (e.g. en_US, es_US).
		assertTrue(visible > 0, "filterLogic=\"contains\" should match locales containing 'US'");
		// ZK-6056 (M3): the bypass preserves the SubModel's default 15-row cap, so
		// even a large model (getAvailableLocales) yields a bounded dropdown.
		assertTrue(visible <= 15, "contains bypass should keep the 15-row cap, got " + visible);
		// Every visible option genuinely contains "US"; none starts with it (no
		// locale toString starts with "US") — which is precisely why startsWith
		// surfaces nothing and the "contains" logic is required.
		for (int i = 0; i < options.length(); i++) {
			JQuery option = options.eq(i);
			if (isVisible(option)) {
				String text = option.text().toUpperCase();
				assertTrue(text.contains("US"), "visible option should contain 'US': " + option.text());
				assertFalse(text.startsWith("US"), "no locale starts with 'US' (proves contains, not startsWith): " + option.text());
			}
		}
		assertNoJSError();
	}

	private long countVisible(JQuery items) {
		int count = 0;
		for (int i = 0; i < items.length(); i++) {
			if (isVisible(items.eq(i))) count++;
		}
		return count;
	}

	private boolean isVisible(JQuery el) {
		return !"none".equals(el.css("display")) && !"hidden".equals(el.css("visibility"));
	}
}
