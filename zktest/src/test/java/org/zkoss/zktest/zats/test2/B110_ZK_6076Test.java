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
		int gb1Bottom = gb1Top + jq("$gb1").outerHeight();
		assertTrue(gb1Bottom <= gb2Top,
				"gb1 (bottom=" + gb1Bottom + ") should not overlap gb2 (top=" + gb2Top + ")");
		assertNotSqueezed("gb1");
		assertNotSqueezed("gb2");

		// a fixed-height container keeps the even split, even with an overflowing child
		assertEquals("100", getEval("jq('$fixed1')[0].offsetHeight"));
		assertEquals("100", getEval("jq('$fixed2')[0].offsetHeight"));

		// a min-height-only container with spare space keeps the even split
		assertEquals("150", getEval("jq('$minh1')[0].offsetHeight"));
		assertEquals("150", getEval("jq('$minh2')[0].offsetHeight"));

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

	@Test
	public void testWindowRerender() {
		connect();
		waitResponse();
		assertNotSqueezed("win1");
		assertNotSqueezed("win2");

		click(jq("$btnWin"));
		waitResponse();
		assertNoJSError();
		assertNotSqueezed("win1");
		assertNotSqueezed("win2");
		assertEquals("true", getEval("(function (n) {n.scrollIntoView({block: 'center'});"
				+ "var r = n.getBoundingClientRect();"
				+ "return document.elementsFromPoint(r.left + r.width / 2, r.top + r.height / 2).indexOf(n) >= 0;"
				+ "})(jq('$winLabel')[0])"), "window content should remain visible after rerender");
	}

	@Test
	public void testSclassUpdate() {
		connect();
		waitResponse();
		assertNotSqueezed("natural1");
		assertNotSqueezed("natural2");

		click(jq("$btnSclass"));
		waitResponse();
		assertTrue(jq("$natural").hasClass("updated"), "container class should be updated");
		assertNotSqueezed("natural1");
		assertNotSqueezed("natural2");
	}

	@Test
	public void testLateContentVisibleWithResize() {
		connect();
		waitResponse();
		assertFalse(jq("$late1c").isVisible());
		assertFalse(jq("$late2c").isVisible());

		click(jq("$show"));
		waitResponse();
		assertNoJSError();
		assertLateContentFits();
	}

	@Test
	public void testLateContentAppended() {
		connect();
		waitResponse();
		click(jq("$appendLate"));
		waitResponse();
		assertNoJSError();
		assertLateContentFits();
	}

	@Test
	public void testSizeCheckPreservesScroll() {
		connect();
		waitResponse();
		eval("jq('$scrollHost')[0].scrollTop = 600");
		assertEquals("600", getEval("jq('$scrollHost')[0].scrollTop"));
		eval("zWatch.fireDown('onSize', zk.Widget.$('$scrollFlex'))");
		assertEquals("600", getEval("jq('$scrollHost')[0].scrollTop"),
				"temporary flex measurement should preserve the ancestor's scroll position");
	}

	@Test
	public void testContainerHeightChange() {
		connect();
		waitResponse();
		eval("(function (w) {w.setHeight('100px'); zUtl.fireSized(w);})(zk.Widget.$('$natural'))");
		assertEquals("50", getEval("jq('$natural1')[0].offsetHeight"));
		assertEquals("50", getEval("jq('$natural2')[0].offsetHeight"));
		eval("(function (w) {w.setHeight(''); zUtl.fireSized(w);})(zk.Widget.$('$natural'))");
		assertNotSqueezed("natural1");
		assertNotSqueezed("natural2");
	}

	private void assertLateContentFits() {
		assertNotSqueezed("late1");
		assertNotSqueezed("late2");
		assertTrue(jq("$late1").offsetTop() + jq("$late1").outerHeight() <= jq("$late2").offsetTop(),
				"late content should stack without overlap");
		assertEquals("200", getEval("jq('$late')[0].clientHeight"),
				"the container should grow to enclose both blocks");
	}
}
