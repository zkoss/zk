/* B110_ZK_6150Test.java

        Purpose:

        Description:

        History:
                Wed Aug 19 10:12:33 CST 2026, Created by peakerlee

Copyright (C) 2026 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.Keys;
import org.zkoss.test.webdriver.WebDriverTestCase;

public class B110_ZK_6150Test extends WebDriverTestCase {

	private static final String POPUP_OPEN =
			"(function(){var p = zk.Widget.$('$dr')._rangePopup, n = p && p.$n();"
			+ "return !!(p && p._isOpen && n && n.style.display !== 'none');})()";

	private static final String FOCUS_IN_POPUP =
			"(function(){var n = zk.Widget.$('$dr')._rangePopup.$n(), a = document.activeElement;"
			+ "return !!(n && a && n.contains(a));})()";

	private boolean popupOpen() {
		return Boolean.parseBoolean(getEval(POPUP_OPEN));
	}

	private void openPopupViaButton() {
		click(jq(".z-daterangebox-button"));
		waitResponse();
	}

	/** Escape aimed at the calendar must still close it. */
	@Test
	public void testEscapeInsidePopupStillClosesIt() {
		connect("/test2/B110-ZK-6150.zul");
		waitResponse();

		openPopupViaButton();
		assertTrue(popupOpen(), "pre-condition: the calendar popup must be open");
		assertTrue(Boolean.parseBoolean(getEval(FOCUS_IN_POPUP)),
				"pre-condition: focus must be inside the popup, otherwise Escape is not aimed at it");

		getActions().sendKeys(Keys.ESCAPE).perform();
		waitResponse();

		assertFalse(popupOpen(), "Escape aimed at the calendar must still close it");
	}

	/** Pointer dismissal now goes through the framework's onFloatUp. The click
	 *  target sits above the box so the popup cannot overlap it. */
	@Test
	public void testOutsideClickStillClosesPopup() {
		connect("/test2/B110-ZK-6150.zul");
		waitResponse();

		openPopupViaButton();
		assertTrue(popupOpen(), "pre-condition: the calendar popup must be open");

		click(jq("$lblTitle"));
		waitResponse();

		assertFalse(popupOpen(), "an outside click must close the popup through onFloatUp");
	}

	/** Focus-out dismissal now goes through onFloatUp({triggerByFocus: true}). */
	@Test
	public void testFocusMovingToAnotherInputClosesPopup() {
		connect("/test2/B110-ZK-6150.zul");
		waitResponse();

		openPopupViaButton();
		assertTrue(popupOpen(), "pre-condition: the calendar popup must be open");

		eval("jq('$tb')[0].focus()");
		waitResponse();

		assertFalse(popupOpen(),
				"moving focus to another widget must close the popup through onFloatUp");
	}

	/** The component must not install a second document mousedown. Focus-out keeps
	 *  a listener: onFloatUp misses focus landing on plain, unfocusable markup. */
	@Test
	public void testNoDuplicatePointerListener() {
		connect("/test2/B110-ZK-6150.zul");
		waitResponse();

		openPopupViaButton();
		assertTrue(popupOpen(), "pre-condition: the calendar popup must be open");

		assertFalse(Boolean.parseBoolean(getEval(
						"!!zk.Widget.$('$dr')._rangePopup._outsideHandler")),
				"pointer dismissal must go through onFloatUp, not a second document mousedown");
	}

	/** Escape must work wherever focus sits — e.g. a popup opened server-side —
	 *  so it is gated on canActivate(), not on the event's target. */
	@Test
	public void testEscapeClosesPopupWhenFocusIsOutsideTheBox() {
		connect("/test2/B110-ZK-6150.zul");
		waitResponse();

		openPopupViaButton();
		assertTrue(popupOpen(), "pre-condition: the calendar popup must be open");

		// blur fires no focusin, so only the Escape below can close the popup
		eval("document.activeElement && document.activeElement.blur()");
		assertTrue(popupOpen(), "pre-condition: blurring alone must not close the popup");
		assertFalse(Boolean.parseBoolean(getEval(FOCUS_IN_POPUP)),
				"pre-condition: focus must be outside the popup for this to mean anything");

		getActions().sendKeys(Keys.ESCAPE).perform();
		waitResponse();

		assertFalse(popupOpen(), "Escape must still close the popup with focus outside it");
	}

	private static final String SCROLL_PAGE = "/test2/B110-ZK-6150-scroll.zul";

	private long evalLong(String jsExpression) {
		return Long.parseLong(getEval(jsExpression).trim());
	}

	/** The vertical gap the placement left between the box and the popup, in viewport px. */
	private long popupGapBelowBox() {
		return evalLong(
				"(function(){var p = zk.Widget.$('$dr')._rangePopup.$n().getBoundingClientRect(),"
				+ " b = jq('$dr')[0].getBoundingClientRect();"
				+ "return Math.round(p.top - b.bottom);})()");
	}

	private long boxViewportTop() {
		return evalLong("Math.round(jq('$dr')[0].getBoundingClientRect().top)");
	}

	private void scrollContainerTo(int top) {
		eval("jq('$sc')[0].scrollTop = " + top);
		sleep(300);
		waitResponse();
	}

	/** The popup is placed once, in document coordinates, so scrolling the container
	 *  it sits in must re-run that placement — otherwise it is left behind over
	 *  unrelated content while still writing back to the box. */
	@Test
	public void testPopupFollowsBoxWhenContainerScrolls() {
		connect(SCROLL_PAGE);
		waitResponse();

		assertTrue(evalLong("window.innerWidth") > 576,
				"pre-condition: below 576px the popup is a position:fixed full-screen sheet, "
						+ "so its anchoring cannot be measured");

		openPopupViaButton();
		assertTrue(popupOpen(), "pre-condition: the calendar popup must be open");

		long gapBefore = popupGapBelowBox(), boxTopBefore = boxViewportTop();
		assertTrue(gapBefore >= 0 && gapBefore <= 4,
				"pre-condition: the popup must open right below the box, not viewport-clamped; gap="
						+ gapBefore);

		scrollContainerTo(20);

		assertTrue(popupOpen(), "the box is still in view, so the popup must stay open");
		assertEquals(boxTopBefore - 20, boxViewportTop(),
				"pre-condition: the container must really have scrolled the box");
		long gapAfter = popupGapBelowBox();
		assertTrue(Math.abs(gapAfter - gapBefore) <= 1,
				"the popup must follow the box while its container scrolls; gap went from "
						+ gapBefore + " to " + gapAfter);
	}

	/** Once the box has scrolled out of its container there is nothing left to
	 *  anchor to, so the popup closes instead of floating over other content. */
	@Test
	public void testPopupClosesWhenBoxScrollsOutOfContainer() {
		connect(SCROLL_PAGE);
		waitResponse();

		openPopupViaButton();
		assertTrue(popupOpen(), "pre-condition: the calendar popup must be open");

		scrollContainerTo(400);

		assertFalse(popupOpen(), "the popup must close once its box has scrolled out of view");
	}

	private static final String RESIZE_PAGE = "/test2/B110-ZK-6150-resize.zul";

	private void resizeWindowWidthTo(int width) {
		Dimension size = driver.manage().window().getSize();
		driver.manage().window().setSize(new Dimension(width, size.height));
		// zk debounces the browser resize and only then fires the onSize watch
		sleep(500);
		waitResponse();
	}

	/** A resize reflows the page and moves the box without firing any scroll, so
	 *  the placement open() made once in document coordinates has to be re-run —
	 *  otherwise the popup is left hanging away from the box it writes back to. */
	@Test
	public void testPopupFollowsBoxWhenWindowResizes() {
		connect(RESIZE_PAGE);
		waitResponse();

		int startWidth = driver.manage().window().getSize().width;
		assertTrue(startWidth >= 900,
				"pre-condition: the window must be wide enough to narrow by 300px and stay "
						+ "above the 576px full-screen breakpoint; width=" + startWidth);

		openPopupViaButton();
		assertTrue(popupOpen(), "pre-condition: the calendar popup must be open");

		long gapBefore = popupGapBelowBox(), boxTopBefore = boxViewportTop();
		assertTrue(gapBefore >= 0 && gapBefore <= 4,
				"pre-condition: the popup must open right below the box, not viewport-clamped; gap="
						+ gapBefore);

		resizeWindowWidthTo(startWidth - 300);

		assertTrue(evalLong("window.innerWidth") > 576,
				"pre-condition: below 576px the popup is a position:fixed full-screen sheet, "
						+ "so its anchoring cannot be measured");
		long boxTopAfter = boxViewportTop();
		assertTrue(boxTopAfter <= boxTopBefore - 20,
				"pre-condition: narrowing the window must really move the box up; top went from "
						+ boxTopBefore + " to " + boxTopAfter);
		assertTrue(popupOpen(), "a resize must not dismiss the popup");
		long gapAfter = popupGapBelowBox();
		assertTrue(Math.abs(gapAfter - gapBefore) <= 1,
				"the popup must follow the box when the window is resized; gap went from "
						+ gapBefore + " to " + gapAfter);
	}

	// ----- the range panels share zul.db.Calendar with every plain Datebox -----

	/**
	 * The range popup reaches its panels by augmenting `zul.db.Calendar.prototype`,
	 * which every plain `Datebox` on the page runs through as well
	 * (`zul.db.CalendarPop` inherits `_markCal` / `bind_` and chains `super` for
	 * `_setView` / `doKeyDown_`). So the datebox must keep `z-calendar` as its zclass, must move its day
	 * cursor and switch views exactly as before, and must never pick up a
	 * `z-cell-range-*` role.
	 */
	@Test
	public void testPlainDateboxIsUnaffectedByTheRangePanelAugments() {
		connect("/test2/B110-ZK-6150.zul");
		waitResponse();

		// eval() wraps its argument in parentheses, so it only takes one expression.
		eval("(function(){window.__err = [];"
				+ "window.onerror = function (m) { window.__err.push(String(m)); };})()");
		eval("zk.Widget.$('$db').setOpen(true)");
		waitResponse();

		// The subclass CE itself uses does NOT rename the zclass, because widgetName
		// is only set by the WPD's zkreg and CalendarPop is not in it. Any refactor
		// that registers a panel subclass instead would move these two strings and
		// silently orphan every z-calendar-* selector and all of calendar.less.
		assertEquals("zul.db.CalendarPop", getEval("zk.Widget.$('$db')._pop.className"),
				"pre-condition: the plain datebox popup is the CE Calendar subclass");
		assertEquals("z-calendar", getEval("zk.Widget.$('$db')._pop.getZclass()"),
				"a Calendar subclass must keep the base zclass, or the LESS stops matching");
		assertEquals("z-calendar-mid", getEval("zk.Widget.$('$db')._pop.$s('mid')"),
				"$s() must keep deriving z-calendar-* for a Calendar subclass");

		// doKeyDown_ + _markCal: one day right, then one week down.
		eval("window.__t0 = zk.Widget.$('$db')._pop.getTime().getTime()");
		eval("zk.Widget.$('$db').getInputNode().focus()");
		getActions().sendKeys(Keys.ARROW_RIGHT).perform();
		getActions().sendKeys(Keys.ARROW_DOWN).perform();
		waitResponse();
		assertEquals(8L, evalLong("Math.round((zk.Widget.$('$db')._pop.getTime().getTime()"
						+ " - window.__t0) / 86400000)"),
				"the day cursor must still move 1 day + 1 week on the plain datebox");
		assertEquals(1L, evalLong("(function(){var c = zk.Widget.$('$db')._pop,"
				+ " m = c.$n('mid'), s = m && m.querySelectorAll('.' + c.$s('selected'));"
				+ "return s ? s.length : 0;})()"),
				"and must still mark exactly one cursor cell");

		// _setView, including the animated `force` shift the augment reads $n('mid') for.
		eval("zk.Widget.$('$db')._pop._setView('month')");
		waitResponse();
		assertEquals("month", getEval("zk.Widget.$('$db')._pop._view"),
				"the plain datebox must still switch to the month view");
		assertEquals(12L, evalLong(
				"zk.Widget.$('$db')._pop.$n('mid').querySelectorAll('td').length"),
				"the month view must still draw its 12 cells");
		eval("zk.Widget.$('$db')._pop._shift(1)");
		waitResponse();
		eval("zk.Widget.$('$db')._pop._setView('day')");
		waitResponse();
		assertEquals(42L, evalLong(
				"zk.Widget.$('$db')._pop.$n('mid').querySelectorAll('td').length"),
				"drilling back into the day view must still draw the full grid");
		assertTrue(evalLong(
				"zk.Widget.$('$db')._pop.$n('mid').querySelectorAll('[class*=\"z-cell-range\"]').length") == 0,
				"a plain datebox must never be painted with the popup's range roles");
		assertEquals(1L, evalLong("(function(){var c = zk.Widget.$('$db')._pop;"
				+ "return c._subnodes['mid'] === c.$n('mid') ? 1 : 0;})()"),
				"the augment's pre-read of $n('mid') must not leave a stale node cached");

		// The guard has to have run — otherwise this whole test proves nothing —
		// and it has to have answered false every single time.
		assertEquals("", getEval("window.__err.join('|')"), "no JS error on the plain datebox");
	}

	// ----- Escape: the document listener vs the framework's escPressed_ -----

	/**
	 * The other half of the question: the document listener must not be too wide
	 * either. With a modal `Window` stacked over it the popup is not the active
	 * float, `canActivate({checkOnly: true})` says so (`zk/widget.ts:4514`), and
	 * Escape has to stay with the modal.
	 */
	@Test
	public void testEscapeStaysWithAModalStackedOverThePopup() {
		connect("/test2/B110-ZK-6150.zul");
		waitResponse();

		click(jq("$btnModal"));
		waitResponse();
		assertTrue(Boolean.parseBoolean(getEval("!!zk.currentModal")),
				"pre-condition: the modal window must own the desktop");

		// Server-side open, so the popup can appear under a modal that already has
		// focus — the ordering a user cannot produce by clicking.
		eval("zk.Widget.$('$dr')._openPopup()");
		waitResponse();
		assertTrue(popupOpen(), "pre-condition: the calendar popup must be open");

		assertFalse(Boolean.parseBoolean(getEval(
						"zk.Widget.$('$dr')._rangePopup.canActivate({checkOnly: true})")),
				"pre-condition: a popup under a modal must not own activation");

		getActions().sendKeys(Keys.ESCAPE).perform();
		waitResponse();

		assertTrue(popupOpen(),
				"Escape belongs to the modal, so the popup must stay open instead of "
						+ "closing just because the key reached the document");
	}
}
