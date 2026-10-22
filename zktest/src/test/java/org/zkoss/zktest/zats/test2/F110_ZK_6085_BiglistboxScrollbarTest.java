/* F110_ZK_6085_BiglistboxScrollbarTest.java

	Purpose:
		The Biglistbox scrollbars lie inside the biglistbox.

	Description:
		The body is sized from the height inside the biglistbox's border. Sizing it
		from the height including the border pushed the horizontal scrollbar partly
		outside, where the biglistbox clips it.

	History:
		Fri Oct 02 2026, Created by peggypeng

Copyright (C) 2026 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import java.util.Arrays;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;

public class F110_ZK_6085_BiglistboxScrollbarTest extends WebDriverTestCase {
	/** Sub-pixel layout can put touching boxes a fraction of a pixel apart. */
	private static final double EPSILON = 0.5;

	private static final int LEFT = 0, TOP = 1, RIGHT = 2, BOTTOM = 3;

	@Test
	public void fixedHeight() {
		connect();
		waitResponse();
		assertScrollbarsInside("fixed");
	}

	@Test
	public void minHeight() {
		connect();
		waitResponse();
		assertScrollbarsInside("min");
	}

	private static void assertScrollbarsInside(String id) {
		double[] clip = paddingBoxOf("$" + id);
		for (String bar : new String[] {"vertical", "horizontal"}) {
			double[] r = rectOf("$" + id + " .z-biglistbox-wscroll-" + bar);
			Assertions.assertTrue(r[LEFT] >= clip[LEFT] - EPSILON && r[TOP] >= clip[TOP] - EPSILON
							&& r[RIGHT] <= clip[RIGHT] + EPSILON && r[BOTTOM] <= clip[BOTTOM] + EPSILON,
					id + ": the " + bar + " scrollbar must lie inside the biglistbox, which clips it, bar "
							+ Arrays.toString(r) + " biglistbox " + Arrays.toString(clip));
		}
	}

	private static double[] rectOf(String selector) {
		return toDoubles(getEval("(function () { var r = jq('" + selector + "')[0].getBoundingClientRect();"
				+ " return [r.left, r.top, r.right, r.bottom].join(); })()"));
	}

	/** The box inside the border, which is what {@code overflow: hidden} clips to. */
	private static double[] paddingBoxOf(String selector) {
		return toDoubles(getEval("(function () { var n = jq('" + selector + "')[0],"
				+ " r = n.getBoundingClientRect(), s = getComputedStyle(n);"
				+ " return [r.left + parseFloat(s.borderLeftWidth), r.top + parseFloat(s.borderTopWidth),"
				+ " r.right - parseFloat(s.borderRightWidth), r.bottom - parseFloat(s.borderBottomWidth)].join(); })()"));
	}

	private static double[] toDoubles(String csv) {
		return Arrays.stream(csv.split(",")).mapToDouble(Double::parseDouble).toArray();
	}
}
