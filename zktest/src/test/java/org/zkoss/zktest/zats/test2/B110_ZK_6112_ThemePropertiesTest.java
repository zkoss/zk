/* B110_ZK_6112_ThemePropertiesTest.java

	Purpose:

	Description:

	History:
		Mon Oct 05 2026, Created for ZK-6112.

Copyright (C) 2026 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import org.zkoss.lang.Library;
import org.zkoss.test.webdriver.WebDriverTestCase;

/**
 * The zk.xml library properties org.zkoss.theme.marble.density and
 * org.zkoss.theme.marble.brand are rendered into the page itself, so Marble's density and
 * brand apply from the first paint, and the runtime helpers can still override them (ZK-6112).
 */
public class B110_ZK_6112_ThemePropertiesTest extends WebDriverTestCase {
	private static final String DENSITY = "org.zkoss.theme.marble.density";
	private static final String BRAND = "org.zkoss.theme.marble.brand";

	@AfterEach
	public void resetProperties() {
		Library.setProperty(DENSITY, null);
		Library.setProperty(BRAND, null);
	}

	@Test
	public void testNothingConfigured() {
		connect();
		waitResponse();
		assertEquals("null", rootAttribute("data-density"));
		assertEquals("null", rootAttribute("data-brand"));
		assertEquals(-1, primaryStyleIndex());
		assertNoAnyError();
	}

	@Test
	public void testCompactDensity() {
		Library.setProperty(DENSITY, "compact");
		connect();
		waitResponse();
		assertEquals("compact", rootAttribute("data-density"));
		assertEquals("32px", rootToken("--zk-input-height"));
		assertNoAnyError();
	}

	@Test
	public void testBrandPreset() {
		Library.setProperty(BRAND, "slate");
		connect();
		waitResponse();
		assertEquals("slate", rootAttribute("data-brand"));
		assertEquals("#506274", rootToken("--zk-color-primary"));
		assertNoAnyError();
	}

	@Test
	public void testHexBrand() {
		Library.setProperty(BRAND, "#0a7d5a");
		connect();
		waitResponse();
		assertEquals("null", rootAttribute("data-brand"));
		assertEquals("#0a7d5a", rootToken("--zk-color-primary"));
		int wcs = Integer.parseInt(getEval("Array.from(document.head.children).findIndex(function (e) {"
				+ " return e.tagName === 'LINK' && e.href.indexOf('/zul/css/zk.wcs') >= 0; })"));
		int style = primaryStyleIndex();
		assertTrue(wcs >= 0, "zk.wcs is linked");
		assertTrue(style > wcs, "the primary-seed style follows zk.wcs: style=" + style + ", wcs=" + wcs);
		assertNoAnyError();
	}

	@Test
	public void testRuntimeBrandOverridesHexBrand() {
		Library.setProperty(BRAND, "#0a7d5a");
		connect();
		waitResponse();
		click(jq("$copper"));
		waitResponse();
		assertEquals("copper", rootAttribute("data-brand"));
		assertEquals("#b45309", rootToken("--zk-color-primary"));
		assertNoAnyError();
	}

	private static String rootAttribute(String name) {
		return getEval("String(document.documentElement.getAttribute('" + name + "'))");
	}

	private static String rootToken(String name) {
		return getEval("getComputedStyle(document.documentElement).getPropertyValue('" + name + "').trim()");
	}

	/** The index among the head's children of the style that overrides the primary seed, or -1. */
	private static int primaryStyleIndex() {
		return Integer.parseInt(getEval("Array.from(document.head.children).findIndex(function (e) {"
				+ " return e.tagName === 'STYLE' && e.textContent.indexOf(':root{--zk-color-primary:') >= 0; })"));
	}
}
