/* B110_ZK_6192Test.java

	Purpose:

	Description:

	History:
		Tue Oct 13 2026, Created by hawkchen

Copyright (C) 2026 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;

/**
 * The tooltip of a vertical slider flips to the left of the knob when the right side has no room.
 */
public class B110_ZK_6192Test extends WebDriverTestCase {
	private static final String KNOB = " .z-sliderbuttons-button";
	private static final String TOOLTIP = " .z-sliderbuttons-tooltip";

	private void hoverKnob(String slider, int index) {
		getActions().moveToElement(toElement(jq(slider + KNOB).eq(index))).perform();
		waitResponse();
		assertTrue(jq(slider + TOOLTIP).eq(index).isVisible(), "hovering the knob should reveal its tooltip");
	}

	// left, right of the n-th tooltip and knob, as "tl,tr,kl,kr"
	private double[] rects(String slider, int index) {
		String[] v = getEval("(function(){var t=jq('" + slider + TOOLTIP + "')[" + index + "].getBoundingClientRect(),"
				+ "k=jq('" + slider + KNOB + "')[" + index + "].getBoundingClientRect();"
				+ "return [t.left,t.right,k.left,k.right].join(',');})()").split(",");
		double[] r = new double[4];
		for (int i = 0; i < 4; i++)
			r[i] = Double.parseDouble(v[i]);
		return r;
	}

	private void assertInside(String slider, int index, double minLeft, double maxRight, boolean mustBeLeft) {
		hoverKnob(slider, index);
		double[] r = rects(slider, index);
		assertTrue(!mustBeLeft || r[1] <= r[2] + 1, slider + " knob " + index + ": tooltip should be on the left of the knob, tooltip right "
				+ r[1] + " knob left " + r[2]);
		assertTrue(r[0] >= minLeft - 1 && r[1] <= maxRight + 1, slider + " knob " + index
				+ ": tooltip [" + r[0] + "," + r[1] + "] should be inside [" + minLeft + "," + maxRight + "]");
	}

	private double viewportWidth() {
		return Double.parseDouble(getEval("document.documentElement.clientWidth"));
	}

	private double[] boxRange() {
		String[] v = getEval("(function(){var e=jq('$box')[0],b=e.getBoundingClientRect();return [b.left+e.clientLeft,b.left+e.clientLeft+e.clientWidth].join(',');})()")
				.split(",");
		return new double[] {Double.parseDouble(v[0]), Double.parseDouble(v[1])};
	}

	@Test
	public void viewportEdge() {
		connect();
		waitResponse();
		for (int i = 0; i < 2; i++)
			assertInside("$rs", i, 0, viewportWidth(), true);
	}

	@Test
	public void clippedContainer() {
		connect();
		waitResponse();
		double[] box = boxRange();
		for (int i = 0; i < 2; i++)
			// the tooltip of "5" is narrow enough to fit on the right, "500" is not
			assertInside("$ms", i, box[0], box[1], i == 1);
	}

	@Test
	public void controlStaysOnTheRight() {
		connect();
		waitResponse();
		for (int i = 0; i < 2; i++) {
			hoverKnob("$ctrl", i);
			double[] r = rects("$ctrl", i);
			assertTrue(r[0] >= r[3] - 1, "tooltip should stay on the right of the knob, tooltip left "
					+ r[0] + " knob right " + r[3]);
		}
	}
}
