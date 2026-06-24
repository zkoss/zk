/* B104_ZK_6076Test.java

        Purpose:
                
        Description:
                
        History:
                Tue Apr 14 15:19:57 CST 2026, Created by peakerlee

Copyright (C) 2026 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

public class B104_ZK_6076Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		waitResponse();

		// Initially gb1 is invisible; only gb2 ("second") is visible
		JQuery gb1 = jq("$gb1");
		JQuery gb2 = jq("$gb2");
		assertTrue(!gb1.isVisible(), "gb1 should be invisible initially");
		assertTrue(gb2.isVisible(), "gb2 should be visible initially");

		// Click button to toggle gb1 visibility
		click(jq("$btn"));
		waitResponse();

		// Now both groupboxes should be visible
		assertTrue(gb1.isVisible(), "gb1 should be visible after click");
		assertTrue(gb2.isVisible(), "gb2 should be visible after click");

		// The inner groupbox "init close" should be rendered INSIDE gb1,
		// meaning its top position is between gb1's top and gb2's top.
		JQuery gbInner = jq("$gbInner");
		assertTrue(gbInner.exists(), "inner groupbox should exist");

		// Lock down the DOM hierarchy too: gbInner must remain a descendant of gb1.
		// Without this, a regression that detaches gbInner from gb1 but happens to
		// land it visually between gb1 and gb2 would silently pass the offsetTop checks.
		assertTrue(Boolean.parseBoolean(getEval("jq('$gb1').has(jq('$gbInner')).length > 0")),
				"inner groupbox should remain a descendant of gb1");

		int gb1Top = gb1.offsetTop();
		int gbInnerTop = gbInner.offsetTop();
		int gb2Top = gb2.offsetTop();

		// gb1 should be above gbInner, and gbInner should be above gb2
		assertTrue(gb1Top < gbInnerTop,
				"gb1 (top=" + gb1Top + ") should be above inner groupbox (top=" + gbInnerTop + ")");
		assertTrue(gbInnerTop < gb2Top,
				"inner groupbox (top=" + gbInnerTop + ") should be above gb2 (top=" + gb2Top + ")");

		// M2: a title with an XML special char must render once-decoded ("A & B"),
		// not double-encoded ("A &amp; B"). The mold previously encoded twice, so its
		// initial paint disagreed with setTitle's textContent fast path.
		assertEquals("A & B",
				getEval("jq('$gbAmp').find('.z-groupbox-title-content').text()"),
				"special-char title should render once-decoded, not double-encoded");

		// H0: the bound title of gbA11y went 0 -> 1 via setTitle's in-place fast path
		// (gbA11y stays visible/bound, so bind_ does not re-run). When za11y is active,
		// the closable header's accessible 'title' attribute must track that change.
		if (Boolean.parseBoolean(getEval("!!window.za11y"))) {
			String headerTitle = getEval("jq('$gbA11y').find('.z-groupbox-title').attr('title')");
			assertTrue(headerTitle != null && headerTitle.startsWith("1"),
					"za11y header title should reflect the updated title 1 (was: " + headerTitle + ")");
		}
	}
}
