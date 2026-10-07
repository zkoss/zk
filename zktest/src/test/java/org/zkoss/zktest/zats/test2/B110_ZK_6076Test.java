/* B110_ZK_6076Test.java

        Purpose:

        Description:

        History:
                Tue Apr 14 15:19:57 CST 2026, Created by peakerlee

Copyright (C) 2026 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.zkoss.test.webdriver.WebDriverTestCase;

public class B110_ZK_6076Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		waitResponse();

		// the initial render must not squeeze a vflex child of a content-sized container
		assertFalse(jq("$gb1").isVisible(), "gb1 should be invisible initially");
		assertNotSqueezed("gb2");

		click(jq("$btn"));
		waitResponse();
		assertNoJSError();

		// expected order: 1 -> init close -> second (document coordinates)
		int gb1Top = jq("$gb1").offsetTop();
		int gbInnerTop = jq("$gbInner").offsetTop();
		int gb2Top = jq("$gb2").offsetTop();
		assertTrue(gb1Top < gbInnerTop,
				"gb1 (top=" + gb1Top + ") should be above inner groupbox (top=" + gbInnerTop + ")");
		assertTrue(gbInnerTop < gb2Top,
				"inner groupbox (top=" + gbInnerTop + ") should be above gb2 (top=" + gb2Top + ")");
		assertNotSqueezed("gb1");
		assertNotSqueezed("gb2");

		// a fixed-height container keeps the even split, even with an overflowing child
		assertEquals("100", getEval("jq('$fixed1')[0].offsetHeight"));
		assertEquals("100", getEval("jq('$fixed2')[0].offsetHeight"));
		assertFalse(jq("$fixed").hasClass("z-flex-content"), "a fixed-height container is not content-sized");

		// the title is encoded exactly once: neither double-encoded nor parsed as markup
		assertEquals("A & <b>B</b>", jq("$gbAmp").find(".z-groupbox-title-content").text(),
				"special-char title should render literally");

		// za11y: the closable header's label follows the title update and the open state
		if (Boolean.parseBoolean(getEval("!!window.za11y"))) {
			assertEquals(getEval("'1, ' + msgzul.PANEL_COLLAPSE"),
					jq("$gbA11y").find(".z-groupbox-title").attr("title"));
			assertEquals(getEval("'init close, ' + msgzul.PANEL_EXPAND"),
					jq("$gbInner").find(".z-groupbox-title").attr("title"));
		}
	}

	private void assertNotSqueezed(String id) {
		assertEquals("true", getEval("(function (n) {return n.offsetHeight >= n.scrollHeight;})(jq('$" + id + "')[0])"),
				id + " should not be squeezed below its content");
	}
}
