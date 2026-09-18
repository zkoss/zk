/* Reflow.java

	Purpose:

	Description:

	History:
		Fri Sep 18 14:00:00 CST 2026, Created by peggypeng

Copyright (C) 2026 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.wcag;

import java.util.HashMap;
import java.util.Map;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;

import org.zkoss.test.webdriver.BaseTestCase;

/**
 * Drives a page to the viewport WCAG 1.4.10 Reflow asks for and measures what it does there -
 * https://www.w3.org/WAI/WCAG21/Understanding/reflow
 *
 * <p>1.4.10 asks that content stay usable at <b>320 CSS px</b> - a 1280px viewport at 400% zoom -
 * without two-dimensional scrolling. Vertical scrolling stays allowed, so nothing here measures
 * it, and genuinely 2-D content (data tables, maps, diagrams) is exempt.
 *
 * @author peggypeng
 */
public final class Reflow {

	/** WCAG's reference viewport: 1280 CSS px at 100% zoom. */
	public static final int REFERENCE_WIDTH = 1280;
	/** @see #REFERENCE_WIDTH */
	public static final int REFERENCE_HEIGHT = 1024;
	/** The reference viewport at 400% zoom - 1280/4 - what 1.4.10 requires. */
	public static final int WIDTH = 320;
	/** @see #WIDTH */
	public static final int HEIGHT = 256;

	private final long _layoutWidth;
	private final long _visibleWidth;
	private final long _overflow;
	private final String _offenders;

	private Reflow(long layoutWidth, long visibleWidth, long overflow, String offenders) {
		_layoutWidth = layoutWidth;
		_visibleWidth = visibleWidth;
		_overflow = overflow;
		_offenders = offenders;
	}

	/**
	 * Measures the page as it stands; drive the component and set the viewport first.
	 *
	 * @param excludeSelectors elements to leave out of the offender list, e.g. a grid carrying a
	 * 2-D exception. They change the message only - the verdict is the document's own scroll,
	 * which an excluded element still causes.
	 */
	public static Reflow measure(String... excludeSelectors) {
		return new Reflow(px("window.innerWidth"), px("document.documentElement.clientWidth"),
				px("document.documentElement.scrollWidth - document.documentElement.clientWidth"),
				offenders(excludeSelectors));
	}

	/** Whether the page reflows: no horizontal scrolling, within a pixel of rounding. */
	public boolean conforms() {
		return _overflow <= 1;
	}

	/** How far the document scrolls horizontally, in px. 0 when the page reflows. */
	public long overflow() {
		return _overflow;
	}

	/** What CDP was asked for, scrollbar included; {@link #visibleWidth} is what content occupies. */
	public long layoutWidth() {
		return _layoutWidth;
	}

	/**
	 * The width content can actually occupy. {@code clientWidth} excludes the vertical scrollbar,
	 * so a true 320px viewport measures <b>305</b>. Check "does it fit" against this, not 320.
	 */
	public long visibleWidth() {
		return _visibleWidth;
	}

	/** Reads as an assertion message. */
	public String toString() {
		return conforms()
				? "reflows at " + _layoutWidth + "px"
				: "the page scrolls horizontally at " + _layoutWidth + "px: scrollWidth exceeds "
						+ "clientWidth (" + _visibleWidth + ") by " + _overflow + "px"
						+ (_offenders.isEmpty() ? "" : "; past the right edge: " + _offenders);
	}

	// --- viewport ----------------------------------------------------------------------------

	/**
	 * Switches to a true CSS viewport. Chrome clamps {@code window().setSize()} to ~500px even
	 * headless, so this uses a CDP device-metrics override, which is not; the achieved width is
	 * returned so a caller can assert it rather than silently measure a clamped layout.
	 * {@code mobile} stays false - a mobile UA would change what ZK serves.
	 *
	 * @return the layout viewport width actually reached
	 */
	public static long setViewport(WebDriver driver, int width, int height) {
		Map<String, Object> metrics = new HashMap<>();
		metrics.put("width", width);
		metrics.put("height", height);
		metrics.put("deviceScaleFactor", 1);
		metrics.put("mobile", false);
		((ChromeDriver) driver).executeCdpCommand("Emulation.setDeviceMetricsOverride", metrics);

		// ZK re-lays out from a debounced window resize, not synchronously with the override
		for (int i = 0; i < 40 && px("window.innerWidth") != width; i++) {
			try {
				Thread.sleep(50);
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
				break;
			}
		}
		return px("window.innerWidth");
	}

	/** Undoes {@link #setViewport}. Safe to call when no override is in place. */
	public static void clearViewport(WebDriver driver) {
		if (driver instanceof ChromeDriver)
			((ChromeDriver) driver).executeCdpCommand("Emulation.clearDeviceMetricsOverride",
					new HashMap<>());
	}

	/** Whether this driver can reach a true 320px viewport at all. */
	public static boolean isSupported(WebDriver driver) {
		return driver instanceof ChromeDriver;
	}

	// --- component-level reads ---------------------------------------------------------------

	/**
	 * How many px the first match hides behind its own horizontal scroll. Opt-in per component,
	 * not part of {@link #conforms}: a messagebox body must wrap its prose, a colorbox palette
	 * may scroll.
	 *
	 * @return 0 when nothing is hidden, -1 when nothing matches
	 */
	public static long hiddenContentOf(String selector) {
		return px("(function () {"
				+ "  var n = document.querySelector('" + selector + "');"
				+ "  return n ? n.scrollWidth - n.clientWidth : -1;"
				+ "})()");
	}

	/**
	 * How many elements matching {@code selector} sit outside the viewport - content the user
	 * cannot reach without scrolling sideways. Zero-width elements (a hidden popup) do not count.
	 */
	public static long outsideViewport(String selector) {
		return px("(function () {"
				+ "  var out = 0, all = document.querySelectorAll('" + selector + "');"
				+ "  for (var i = 0; i < all.length; i++) {"
				+ "    var r = all[i].getBoundingClientRect();"
				+ "    if (r.width > 0 && (r.right > document.documentElement.clientWidth + 1 || r.left < -1))"
				+ "      out++;"
				+ "  }"
				+ "  return out;"
				+ "})()");
	}

	/**
	 * Waits until {@code selector} is present, non-zero width, and holding the same width on two
	 * consecutive reads. A popup is sized from its control's box and a CDP viewport change reaches
	 * the page through a debounced resize, so an immediate measurement can catch either.
	 *
	 * @return the settled width, or -1 if it never appeared
	 */
	public static long awaitLaidOut(String selector) {
		long previous = -1;
		for (int i = 0; i < 40; ++i) {
			long width = widthOf(selector);
			if (width > 0 && width == previous)
				return width;
			previous = width;
			try {
				Thread.sleep(50);
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
				break;
			}
		}
		return previous;
	}

	/** The first element matching {@code selector}, as {@code left=.. right=..}, for a message. */
	public static String rectOf(String selector) {
		return BaseTestCase.getEval("(function () {"
				+ "  var n = document.querySelector('" + selector + "');"
				+ "  if (!n) return '(no match)';"
				+ "  var r = n.getBoundingClientRect();"
				+ "  return 'left=' + Math.round(r.left) + ' right=' + Math.round(r.right)"
				+ "       + ' width=' + Math.round(r.width);"
				+ "})()");
	}

	// --- za11y toggling, for before/after pairs ----------------------------------------------

	/**
	 * Strips one declaration from the rules carrying it, reproducing the CSS as it stood before a
	 * fix added it; {@link #restoreDeclarations()} puts it back. Use it to show a test is passing
	 * on the fix rather than trusting the assertion could ever have failed. Matches on exact
	 * {@code selectorText}; cross-origin sheets are skipped.
	 *
	 * @param property the CSS property to remove, e.g. {@code max-width}
	 * @param selectors the rules to remove it from, e.g. {@code .z-colorbox-popup}
	 * @return how many were removed; assert on it, or a typo in a selector reads as a passing
	 * "before" state
	 */
	public static long removeDeclaration(String property, String... selectors) {
		StringBuilder wanted = new StringBuilder("[");
		for (int i = 0; i < selectors.length; ++i)
			wanted.append(i > 0 ? ",'" : "'").append(selectors[i]).append('\'');
		wanted.append(']');

		return px("(function () {"
				+ "  var want = " + wanted + ", prop = '" + property + "';"
				+ "  window.zkReflowStash = [];"
				// @media holds its declarations in a nested cssRules, so the walk must recurse - but
				// not with an early `continue`: since CSS nesting every CSSStyleRule carries a
				// (usually empty) cssRules too, and branching on that skips every plain rule.
				+ "  function walk(rules) {"
				+ "    for (var i = 0; i < rules.length; i++) {"
				+ "      var r = rules[i];"
				+ "      if (r.cssRules && r.cssRules.length) walk(r.cssRules);"
				+ "      if (!r.style || !r.selectorText || want.indexOf(r.selectorText) < 0) continue;"
				+ "      var v = r.style.getPropertyValue(prop);"
				+ "      if (!v) continue;"
				+ "      window.zkReflowStash.push([r, prop, v]);"
				+ "      r.style.removeProperty(prop);"
				+ "    }"
				+ "  }"
				+ "  for (var i = 0; i < document.styleSheets.length; i++) {"
				+ "    try { walk(document.styleSheets[i].cssRules); } catch (e) { /* cross-origin */ }"
				+ "  }"
				+ "  return window.zkReflowStash.length;"
				+ "})()");
	}

	/** Puts back everything {@link #removeDeclaration} took out. @return how many were restored */
	public static long restoreDeclarations() {
		return px("(function () {"
				+ "  var stash = window.zkReflowStash || [];"
				+ "  for (var i = 0; i < stash.length; i++)"
				+ "    stash[i][0].style.setProperty(stash[i][1], stash[i][2]);"
				+ "  window.zkReflowStash = null;"
				+ "  return stash.length;"
				+ "})()");
	}

	/** The computed value of {@code property} on the first element matching {@code selector}. */
	public static String computed(String selector, String property) {
		return BaseTestCase.getEval("(function () {"
				+ "  var n = document.querySelector('" + selector + "');"
				+ "  return n ? getComputedStyle(n).getPropertyValue('" + property + "') : '(no match)';"
				+ "})()");
	}

	/** The rounded width of the first element matching {@code selector}, or -1 if none. */
	public static long widthOf(String selector) {
		return px("(function () {"
				+ "  var n = document.querySelector('" + selector + "');"
				+ "  return n ? n.getBoundingClientRect().width : -1;"
				+ "})()");
	}

	/** The rounded right edge of the first element matching {@code selector}, or -1 if none. */
	public static long rightOf(String selector) {
		return px("(function () {"
				+ "  var n = document.querySelector('" + selector + "');"
				+ "  return n ? n.getBoundingClientRect().right : -1;"
				+ "})()");
	}

	// --- internals ---------------------------------------------------------------------------

	/** Names up to five elements whose right edge is past the viewport, to attribute the scroll. */
	private static String offenders(String... excludeSelectors) {
		StringBuilder excludes = new StringBuilder("[");
		for (int i = 0; i < excludeSelectors.length; ++i)
			excludes.append(i > 0 ? ",'" : "'").append(excludeSelectors[i]).append('\'');
		excludes.append(']');

		return BaseTestCase.getEval("(function () {"
				+ "  var ex = " + excludes + ", out = [], all = document.querySelectorAll('*');"
				+ "  for (var i = 0; i < all.length && out.length < 5; i++) {"
				+ "    var el = all[i], skip = false;"
				+ "    for (var j = 0; j < ex.length; j++)"
				+ "      if (el.matches(ex[j]) || el.closest(ex[j])) { skip = true; break; }"
				+ "    if (skip) continue;"
				+ "    var r = el.getBoundingClientRect();"
				+ "    if (r.width > 0 && r.right > document.documentElement.clientWidth + 1) {"
				+ "      var cls = (el.className && el.className.split) ? el.className.split(' ')[0] : '';"
				+ "      out.push(el.tagName.toLowerCase() + (cls ? '.' + cls : '')"
				+ "             + ' right=' + Math.round(r.right));"
				+ "    }"
				+ "  }"
				+ "  return out.join(' | ');"
				+ "})()");
	}

	/** Evaluates a JS expression and rounds it, so sub-pixel rects come back comparable. */
	private static long px(String expression) {
		return Long.parseLong(BaseTestCase.getEval("Math.round(" + expression + ")"));
	}
}
