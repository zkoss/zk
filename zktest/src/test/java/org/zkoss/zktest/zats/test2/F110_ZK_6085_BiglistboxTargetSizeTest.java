/* F110_ZK_6085_BiglistboxTargetSizeTest.java

	Purpose:
		WCAG 2.5.8 Target Size (Minimum) for the Biglistbox scrollbars and frozen-column tick.

	Description:
		Nothing else scrolls the grid or moves the frozen columns with a pointer, so
		every scrollbar button, both drag handles and the tick have to meet the
		minimum. za11y enlarges them in zkmax/less/_big-a11y.less and starts the
		horizontal track past the tick in zkmax/big-a11y.ts.

		IceBlue only: it asserts the scrollbar buttons, which Marble does not render
		(its thin overlay scrollbar has none). Meeting 2.5.7 there needs a click-on-track
		paging behaviour in WScroll, which is outside the Marble theme.

	History:
		Fri Oct 02 2026, Created by peggypeng

Copyright (C) 2026 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import java.util.Arrays;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

/**
 * WCAG 2.5.8 Target Size (Minimum) - https://www.w3.org/WAI/WCAG22/Understanding/target-size-minimum
 */
@Tag("WcagTestOnly")
@Tag("IceBlueOnly")
public class F110_ZK_6085_BiglistboxTargetSizeTest extends WebDriverTestCase {
	/** The minimum width and height of a pointer target, in CSS px. */
	private static final double MIN = 24;
	/** Sub-pixel layout can put touching boxes a fraction of a pixel apart. */
	private static final double EPSILON = 0.5;
	private static final String[] PARTS = {"home", "up", "body", "down", "end"};
	private static final String TICK = ".z-biglistbox-verticalbar-tick";

	private static final int LEFT = 0, TOP = 1, RIGHT = 2, BOTTOM = 3;

	@Test
	public void scrollbarPartsMeetTheMinimum() {
		connect();
		waitResponse();

		double[] clip = paddingBoxOf("$big");
		for (String bar : new String[] {"vertical", "horizontal"}) {
			for (String part : PARTS) {
				String name = bar + " " + part;
				double[] r = rectOf(part(bar, part));
				assertMinimum(name, r);
				assertInside(name, r, clip);
			}
		}
	}

	@Test
	public void frozenColumnTickMeetsTheMinimumClearOfTheScrollbar() {
		connect();
		waitResponse();

		double[] tick = rectOf(TICK), home = rectOf(part("horizontal", "home"));
		assertMinimum("tick", tick);
		assertInside("tick", tick, paddingBoxOf("$big"));
		Assertions.assertTrue(tick[RIGHT] <= home[LEFT] + EPSILON,
				"the tick must not cover the horizontal scrollbar's first button, tick ends at " + tick[RIGHT]
						+ " and the button starts at " + home[LEFT]);
	}

	@Test
	public void scrollbarButtonsReachTheEnds() {
		connect();
		waitResponse();

		click(inBig(part("vertical", "end")));
		waitResponse();
		Assertions.assertEquals("y = (0,299)", firstCellOf(".z-biglistbox-row:last"),
				"the vertical End button must scroll to the last row");
		click(inBig(part("vertical", "home")));
		waitResponse();
		Assertions.assertEquals("y = (0,0)", firstCellOf(".z-biglistbox-row:first"),
				"the vertical Home button must scroll back to the first row");

		click(inBig(part("horizontal", "end")));
		waitResponse();
		Assertions.assertEquals("Header x = 99", trim(inBig(".z-biglistbox-header:last").text()),
				"the horizontal End button must scroll to the last column");
		click(inBig(part("horizontal", "home")));
		waitResponse();
		Assertions.assertEquals("Header x = 2", trim(inBig(".z-biglistbox-header-leftmost:last").text()),
				"the horizontal Home button must scroll back to the first unfrozen column");
	}

	@Test
	public void draggingTheTickFreezesAnotherColumn() {
		connect();
		waitResponse();

		getActions().clickAndHold(toElement(inBig(TICK)))
				.moveByOffset(130, 0)
				.release()
				.perform();
		waitResponse();
		Assertions.assertEquals(3, inBig(".z-biglistbox-head-frozen .z-biglistbox-header").length(),
				"dragging the tick one column to the right must freeze a third column");
	}

	private static String part(String bar, String part) {
		return ".z-biglistbox-wscroll-" + bar + " .z-biglistbox-wscroll-" + part;
	}

	/** ZK's {@code $id} selector matches nothing once two descendant steps follow it, hence find(). */
	private JQuery inBig(String selector) {
		return jq("$big").find(selector);
	}

	private String firstCellOf(String row) {
		return trim(inBig(".z-biglistbox-body-frozen " + row + " td:first").text());
	}

	private static void assertMinimum(String name, double[] r) {
		double width = r[RIGHT] - r[LEFT], height = r[BOTTOM] - r[TOP];
		Assertions.assertTrue(width >= MIN && height >= MIN,
				name + " must be at least " + MIN + "px square, was " + width + "x" + height);
	}

	private static void assertInside(String name, double[] r, double[] clip) {
		Assertions.assertTrue(r[LEFT] >= clip[LEFT] - EPSILON && r[TOP] >= clip[TOP] - EPSILON
						&& r[RIGHT] <= clip[RIGHT] + EPSILON && r[BOTTOM] <= clip[BOTTOM] + EPSILON,
				name + " must lie inside the biglistbox, which clips it, " + Arrays.toString(r)
						+ " biglistbox " + Arrays.toString(clip));
	}

	private static double[] rectOf(String selector) {
		return toDoubles(getEval("(function () { var r = jq('$big').find('" + selector + "')[0].getBoundingClientRect();"
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
