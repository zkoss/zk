/* B110_ZK_6112_BadgeForcedColorsTest.java

	Purpose:

	Description:

	History:
		Wed Oct 14 2026, Created for ZK-6112.

Copyright (C) 2026 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.chrome.ChromeDriver;
import org.zkoss.test.webdriver.WebDriverTestCase;

/**
 * Marble: under forced-colors the system replaces a badge's background with Canvas, so a dot
 * (no text, no border) used to vanish into the page; the dot is now filled with CanvasText
 * and the count badge keeps its number readable (ZK-6112).
 */
public class B110_ZK_6112_BadgeForcedColorsTest extends WebDriverTestCase {
	@AfterEach
	public void resetForcedColors() {
		emulateForcedColors("none");
	}

	@Test
	public void testDotsAreVisibleMarks() {
		connect();
		waitResponse();
		emulateForcedColors("active");
		assertEquals("true", getEval("String(matchMedia('(forced-colors: active)').matches)"));
		String canvas = systemColor("Canvas");

		for (String dot : new String[] {"wrapped", "corner", "standalone"})
			assertNotEquals(canvas, indicatorStyle(dot, "backgroundColor"), dot + " dot paints Canvas");
	}

	@Test
	public void testCountStaysLegible() {
		connect();
		waitResponse();
		emulateForcedColors("active");
		assertNotEquals(indicatorStyle("count", "backgroundColor"), indicatorStyle("count", "color"));
		assertEquals("5", jq("$count .z-badge-indicator").text());
	}

	private void emulateForcedColors(String value) {
		if (!(getWebDriver() instanceof ChromeDriver))
			return;
		Map<String, Object> feature = new HashMap<>();
		feature.put("name", "forced-colors");
		feature.put("value", value);
		Map<String, Object> media = new HashMap<>();
		media.put("features", List.of(feature));
		((ChromeDriver) getWebDriver()).executeCdpCommand("Emulation.setEmulatedMedia", media);
	}

	private static String indicatorStyle(String badgeId, String property) {
		return getEval("getComputedStyle(zk.Widget.$('$" + badgeId + "').$n()"
				+ ".querySelector('.z-badge-indicator'))." + property);
	}

	/** The colour a system colour keyword resolves to right now. */
	private static String systemColor(String keyword) {
		return getEval("(function () {"
				+ " var probe = document.body.appendChild(document.createElement('i'));"
				+ " probe.style.backgroundColor = '" + keyword + "';"
				+ " var color = getComputedStyle(probe).backgroundColor;"
				+ " document.body.removeChild(probe);"
				+ " return color; })()");
	}
}
