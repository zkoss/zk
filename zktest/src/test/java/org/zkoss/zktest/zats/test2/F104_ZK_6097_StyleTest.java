/* F104_ZK_6097_StyleTest.java

        Purpose:
                
        Description:
                
        History:
                Mon May 11 15:08:19 CST 2026, Created by peakerlee

Copyright (C) 2026 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;

public class F104_ZK_6097_StyleTest extends WebDriverTestCase {

	private static final double CENTER_TOLERANCE_PX = 2.0;

	@Test
	public void test() {
		connect();
		waitResponse();

		String refFamily = jq("$ref").css("font-family");
		String refSize = jq("$ref").css("font-size");

		// 1. Every ZK-6097 component must use ZK's base font-family.
		assertFamily(refFamily, "$av .z-avatar-text", "Avatar text");
		assertFamily(refFamily, "$bd .z-badge-indicator", "Badge indicator");
		assertFamily(refFamily, "$tg", "Chip");
		assertFamily(refFamily, "$bc", "Breadcrumb");
		assertFamily(refFamily, "$cr .z-carousel-arrow-prev", "Carousel arrow");
		// Open the confirmpopup so it has computed styles to inspect.
		click(jq("$btn-cp"));
		waitResponse();
		assertFamily(refFamily, ".z-confirmpopup-message", "Confirmpopup message");

		// 2. Breadcrumb font-size must match the default label.
		assertEquals(refSize, jq("$bc").css("font-size"),
				"Breadcrumb font-size should match a default <label>");

		// 3. Carousel prev / next arrow content must be visually centered.
		// Flex `align-items:center` aligns the *line-box* center with the
		// button center, so a Range-based bounding rect will always look
		// centered. What gives the visual offset is the glyph's own asymmetry
		// around the baseline: the U+2039/U+203A characters used here have a
		// large ascent and (effectively) zero descent, so the rendered ink
		// sits in the upper half of the line-box. We assert the ink is
		// roughly symmetric around the baseline (asc ≈ |desc|), which is the
		// property a fixed implementation must satisfy.
		assertArrowGlyphSymmetric("prev");
		assertArrowGlyphSymmetric("next");
	}

	private void assertFamily(String expected, String selector, String label) {
		String actual = jq(selector).css("font-family");
		assertEquals(expected, actual,
				label + " (" + selector + ") font-family should match default <label>");
	}

	private void assertArrowGlyphSymmetric(String dir) {
		String selector = "$cr .z-carousel-arrow-" + dir;
		String js = "(function(){"
				+ "var el = jq('" + selector + "')[0];"
				+ "if (!el) return '';"
				+ "var cs = getComputedStyle(el);"
				+ "var ctx = document.createElement('canvas').getContext('2d');"
				+ "ctx.font = cs.fontStyle + ' ' + cs.fontWeight + ' '"
				+ "         + cs.fontSize + ' ' + cs.fontFamily;"
				+ "var m = ctx.measureText(el.textContent || '');"
				+ "return m.actualBoundingBoxAscent + ',' + m.actualBoundingBoxDescent;"
				+ "})()";
		String raw = getEval(js);
		assertTrue(raw != null && raw.contains(","),
				"Could not measure carousel " + dir + " arrow glyph metrics");
		String[] parts = raw.split(",");
		double asc = Double.parseDouble(parts[0]);
		double desc = Double.parseDouble(parts[1]);
		double asym = Math.abs(asc - Math.abs(desc));
		assertTrue(asym <= CENTER_TOLERANCE_PX,
				"Carousel " + dir + " arrow glyph is asymmetric around baseline"
						+ " (ascent=" + asc + " descent=" + desc + " asym=" + asym
						+ "px > " + CENTER_TOLERANCE_PX + "px) — flex align-items:center"
						+ " centers the line-box, so an asymmetric glyph appears"
						+ " visually off-center inside the button.");
	}
}
