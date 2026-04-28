/* F110_ZK_6056Test.java

        Purpose:
                
        Description:
                
        History:
                Thu Apr 30 11:09:45 CST 2026, Created by peakerlee

Copyright (C) 2026 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.Keys;

import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

public class F110_ZK_6056Test extends WebDriverTestCase {
	@Test
	public void testFilterLogicContainsAttribute() {
		connect("/test2/F110-ZK-6056.zul");
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
		// no Locale.toString() starts with "US", so the SubModel's startsWith pre-filter shows nothing
		assertTrue(countVisible(options) > 0, "filterLogic=\"contains\" should match locales containing 'US'");
		for (int i = 0; i < options.length(); i++) {
			JQuery option = options.eq(i);
			if (isVisible(option)) {
				String text = option.text().toUpperCase();
				assertTrue(text.contains("US"), "visible option should contain 'US': " + option.text());
				assertFalse(text.startsWith("US"), "no locale starts with 'US' (proves contains, not startsWith): " + option.text());
			}
		}

		// "a" matches hundreds of locales, so only the row cap keeps this at 15
		sendKeys(cbInput, Keys.BACK_SPACE, Keys.BACK_SPACE, "a");
		sleep(500);
		waitResponse();
		assertEquals(15, countVisible(jq(".z-chosenbox-select").find(".z-chosenbox-option")));
		assertNoJSError();
	}

	@Test
	public void testFilterLogicContainsKeepsSubModelRows() {
		connect("/test2/F110-ZK-6056-2.zul");
		waitResponse();
		JQuery cb = jq("@chosenbox").eq(0);
		click(cb);
		waitResponse();
		sendKeys(cb.find(".z-chosenbox-input"), "_");
		sleep(500);
		waitResponse();
		// no locale starts with "_", so only the contains bypass shows rows, bounded by the explicit nRows
		assertEquals(50, countVisible(jq(".z-chosenbox-select").find(".z-chosenbox-option")));
		assertNoJSError();
	}

	@Test
	public void testFilterLogicContainsKeepsSimpleListModelRows() {
		connect("/test2/F110-ZK-6056-3.zul");
		waitResponse();
		JQuery cb = jq("@chosenbox").eq(0);
		click(cb);
		waitResponse();
		sendKeys(cb.find(".z-chosenbox-input"), "_");
		sleep(500);
		waitResponse();
		assertEquals(50, countVisible(jq(".z-chosenbox-select").find(".z-chosenbox-option")));
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
