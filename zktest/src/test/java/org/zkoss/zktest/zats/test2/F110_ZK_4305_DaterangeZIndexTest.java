/* F110_ZK_4305_DaterangeZIndexTest.java

		Purpose:

		Description:

		History:
				Thu Sep  3 11:30:38 CST 2026, Created by peakerlee

Copyright (C) 2026 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.zkoss.test.webdriver.WebDriverTestCase;

/**
 * Regression for the popup stacking order (ZK-4305).
 *
 * <p>{@code DaterangePopup#open} used to call {@code setTopmost()} before
 * {@code setFloating_()}. {@code zk.Widget#setTopmost} walks ancestors looking
 * for {@code _floating}, and only {@code setFloating_} ever sets it — so with
 * the calls in that order it returned -1 and the framework never assigned the
 * popup a z-index at all. What actually raised the popup was a hardcoded
 * {@code z-index: 90000} in {@code daterangebox.less}, invisible to the float
 * stack ({@code _topZIndex} reads inline z-index only).
 *
 * <p>Hit-testability alone cannot tell the two worlds apart — 90000 already
 * outranks the modal mask, so the popup was clickable before the fix too. The
 * discriminating assertion is therefore the source of the z-index: after the
 * fix {@code setFloatZIndex_} writes an INLINE {@code style.zIndex} (the modal
 * band, 1800+) on the popup root; before the fix that inline value is empty and
 * only the class rule's 90000 applies.
 */
public class F110_ZK_4305_DaterangeZIndexTest extends WebDriverTestCase {

	/** The theme value the fix deletes; the float stack never produces it. */
	private static final String THEME_Z_INDEX = "90000";

	private void openPopup() {
		click(jq("$drb .z-daterangebox-button"));
		waitResponse();
		assertEquals(1, jq(".z-daterangebox-popup").length(),
				"Pre-condition: the trigger button opens the popup");
	}

	/**
	 * Control: the popup is reachable to a real pointer inside a modal window.
	 * Green before and after — a rect assertion would be green even for a box
	 * painted under the mask, so this only guards against a regression that
	 * drops the popup out of the stack entirely.
	 */
	@Test
	public void testPopupIsHitTestableAboveTheModalMask() {
		connect("/test2/F110-ZK-4305-zindex.zul");
		waitResponse();

		openPopup();

		String topIsInPopup = getEval(
				"(function () {"
						+ " var p = document.querySelector('.z-daterangebox-popup');"
						+ " var r = p.getBoundingClientRect();"
						+ " var t = document.elementsFromPoint(r.left + r.width / 2, r.top + r.height / 2)[0];"
						+ " return String(!!t && p.contains(t));"
						+ "})()");
		assertEquals("true", topIsInPopup,
				"The centre of the popup must hit-test to the popup itself, not the modal mask");
	}

	/**
	 * Discriminator: the z-index must come from the framework's float stack,
	 * i.e. an inline style written by {@code setFloatZIndex_}. Red before the
	 * fix (inline z-index is empty; only the theme class supplies 90000).
	 */
	@Test
	public void testPopupZIndexComesFromTheFloatStack() {
		connect("/test2/F110-ZK-4305-zindex.zul");
		waitResponse();

		openPopup();

		// String(...) so an unset inline z-index arrives as "" rather than null —
		// a null would sail past assertNotEquals("", …) and hide the regression.
		String inlineZIndex = getEval(
				"String(document.querySelector('.z-daterangebox-popup').style.zIndex || '')");
		assertNotEquals("", inlineZIndex,
				"setTopmost() must run after setFloating_() so the float stack writes an inline z-index");
		assertNotEquals(THEME_Z_INDEX, inlineZIndex,
				"The z-index must be assigned by the float stack, not copied from the removed theme rule");
		assertTrue(Integer.parseInt(inlineZIndex) > 0,
				"The float stack's z-index must be a positive number, was: " + inlineZIndex);

		// And it must still win against the modal window it is opened inside.
		String winZIndex = getEval("String(jq('$win')[0].style.zIndex || 0)");
		assertTrue(Integer.parseInt(inlineZIndex) >= Integer.parseInt(winZIndex),
				"The popup must not stack below its own modal window (popup=" + inlineZIndex
						+ ", window=" + winZIndex + ")");
	}
}
