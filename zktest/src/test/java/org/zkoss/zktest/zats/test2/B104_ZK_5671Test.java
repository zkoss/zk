/* B104_ZK_5671Test.java

		Purpose:

		Description:

		History:
				Thu May 14 10:46:11 CST 2026, Created by peakerlee

Copyright (C) 2026 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

public class B104_ZK_5671Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		waitResponse();

		JQuery container = jq(".z-pdfviewer-container");
		int retries = 0;
		while (!container.find("canvas").exists() && retries < 40) {
			sleep(500);
			retries++;
		}
		assertTrue(container.find("canvas").exists(), "canvas should be rendered");

		// devicePixelRatio used by the browser
		double dpr = Double.parseDouble(
				getEval("(window.devicePixelRatio || 1).toString()"));

		// canvas backing-store width (HTMLCanvasElement.width)
		int canvasWidth = Integer.parseInt(
				getEval("jq('.z-pdfviewer-container canvas')[0].width.toString()"));
		// canvas CSS width
		int canvasCssWidth = Integer.parseInt(
				getEval("Math.round(jq('.z-pdfviewer-container canvas')[0].getBoundingClientRect().width).toString()"));

		// On HiDPI (dpr > 1): backing-store width should be ~ dpr * css width.
		// On non-HiDPI (dpr == 1): they should be roughly equal.
		// Allow a 2px rounding tolerance.
		int expected = (int) Math.floor(canvasCssWidth * dpr);
		assertTrue(Math.abs(canvasWidth - expected) <= 2,
				"canvas backing-store width (" + canvasWidth
						+ ") should match css width * devicePixelRatio (" + expected
						+ "), dpr=" + dpr);

		// Sanity: when dpr > 1, backing-store strictly larger than CSS box.
		if (dpr > 1.0) {
			assertTrue(canvasWidth > canvasCssWidth,
					"on HiDPI, canvas pixel width should exceed CSS width");
		} else {
			assertEquals(canvasCssWidth, canvasWidth,
					"on non-HiDPI, canvas pixel width should equal CSS width");
		}
	}
}
