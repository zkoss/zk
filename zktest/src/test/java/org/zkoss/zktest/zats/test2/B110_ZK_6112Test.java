/* B110_ZK_6112Test.java

	Purpose:

	Description:

	History:
		Fri Sep 11 2026, Created for ZK-6112.

Copyright (C) 2026 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;

import org.zkoss.lang.Library;
import org.zkoss.test.webdriver.WebDriverTestCase;

/**
 * ZK 11 makes Marble the default theme: StandardThemeProvider links the reset stylesheet
 * immediately before the zk.wcs widget-CSS bundle, and the library property
 * org.zkoss.zul.theme.browserDefault selects the host-safe reset-embed.css instead of the
 * global reset.css (ZK-6112).
 */
public class B110_ZK_6112Test extends WebDriverTestCase {
	private static final String BROWSER_DEFAULT = "org.zkoss.zul.theme.browserDefault";

	@Test
	public void testResetPrecedesWcs() {
		connect();
		waitResponse();
		List<String> hrefs = stylesheetHrefs();
		int reset = indexOf(hrefs, "/zul/css/reset.css");
		int wcs = indexOf(hrefs, "/zul/css/zk.wcs");
		assertTrue(wcs >= 0, "zk.wcs is linked: " + hrefs);
		assertTrue(reset >= 0, "reset.css is linked: " + hrefs);
		assertTrue(reset < wcs, "reset.css precedes zk.wcs: " + hrefs);
		assertFalse(indexOf(hrefs, "/zul/css/reset-embed.css") >= 0, "reset-embed.css is not linked by default: " + hrefs);
		assertNoAnyError();
	}

	@Test
	public void testBrowserDefaultServesEmbedReset() {
		Library.setProperty(BROWSER_DEFAULT, "true");
		try {
			connect();
			waitResponse();
			List<String> hrefs = stylesheetHrefs();
			int reset = indexOf(hrefs, "/zul/css/reset-embed.css");
			int wcs = indexOf(hrefs, "/zul/css/zk.wcs");
			assertTrue(wcs >= 0, "zk.wcs is linked: " + hrefs);
			assertTrue(reset >= 0, "reset-embed.css is linked when browserDefault=true: " + hrefs);
			assertTrue(reset < wcs, "reset-embed.css precedes zk.wcs: " + hrefs);
			assertFalse(indexOf(hrefs, "/zul/css/reset.css") >= 0, "reset.css is not linked when browserDefault=true: " + hrefs);
			assertNoAnyError();
		} finally {
			Library.setProperty(BROWSER_DEFAULT, null);
		}
	}

	private static List<String> stylesheetHrefs() {
		return Arrays.asList(getEval(
				"Array.from(document.querySelectorAll('head > link[rel=stylesheet]')).map(function (l) { return l.href; }).join('\\n')")
				.split("\n"));
	}

	private static int indexOf(List<String> hrefs, String part) {
		for (int i = 0; i < hrefs.size(); i++)
			if (hrefs.get(i).contains(part))
				return i;
		return -1;
	}
}
