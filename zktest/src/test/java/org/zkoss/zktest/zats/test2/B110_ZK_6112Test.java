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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

	@Test
	public void testFontFaceWithoutDsp() {
		connect();
		waitResponse();
		String wcs = getEval("(function () { var x = new XMLHttpRequest();"
				+ " x.open('GET', document.querySelector('head > link[href*=\"/zul/css/zk.wcs\"]').href, false);"
				+ " x.send(); return x.responseText; })()");
		assertFalse(wcs.contains("${"), "zk.wcs carries no DSP expression");
		assertFalse(wcs.contains("~./"), "every ~./ url is encoded");
		Matcher m = Pattern.compile("@font-face\\{[^}]*src:url\\(\"([^\"]*/zul/font/inter-latin-variable\\.woff2)\"\\)")
				.matcher(wcs);
		assertTrue(m.find(), "zk.wcs declares the Inter @font-face with an encoded url");
		String status = getEval("(function () { var x = new XMLHttpRequest(); x.open('GET', '" + m.group(1)
				+ "', false); x.send(); return String(x.status); })()");
		assertTrue("200".equals(status), "the encoded font url is served: " + m.group(1) + " -> " + status);
		assertNoAnyError();
	}

	@Test
	public void testImagesServedByUrl() {
		connect();
		waitResponse();
		String wcs = getEval("(function () { var x = new XMLHttpRequest();"
				+ " x.open('GET', document.querySelector('head > link[href*=\"/zul/css/zk.wcs\"]').href, false);"
				+ " x.send(); return x.responseText; })()");
		assertFalse(wcs.contains("data:image"), "no image is inlined as a data URI");
		assertFalse(wcs.contains("~./"), "every ~./ url is encoded");
		// one image each of zul, the Lucide icon set, zkex and zkmax: every url(~./...) of the plain CSS is encoded
		for (String file : new String[] { "zul/img/marble/checkmark.svg", "zul/img/icons/check.svg",
				"zkex/img/marble/colorbox-icon.svg", "zkmax/img/marble/gl-x.svg" }) {
			String pattern = "url\\(\"([^\"]*/" + Pattern.quote(file) + ")\"\\)";
			Matcher m = Pattern.compile(pattern).matcher(wcs);
			assertTrue(m.find(), "zk.wcs holds an encoded url for " + pattern);
			String status = getEval("(function () { var x = new XMLHttpRequest(); x.open('GET', '" + m.group(1)
					+ "', false); x.send(); return String(x.status); })()");
			assertTrue("200".equals(status), "the image is served: " + m.group(1) + " -> " + status);
		}
		assertNoAnyError();
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
