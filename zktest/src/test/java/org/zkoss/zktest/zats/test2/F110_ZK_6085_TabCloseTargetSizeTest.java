/* F110_ZK_6085_TabCloseTargetSizeTest.java

	Purpose:
		WCAG 2.5.8 Target Size (Minimum) for the close button of a closable Tab.

	Description:
		The close button sits inside the tab, and a click anywhere else in the
		tab selects it, so the close button has to meet the minimum size on its
		own. za11y enlarges it in zul/less/_tab-a11y.less.

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

/**
 * WCAG 2.5.8 Target Size (Minimum) - https://www.w3.org/WAI/WCAG22/Understanding/target-size-minimum
 */
@Tag("WcagTestOnly")
public class F110_ZK_6085_TabCloseTargetSizeTest extends WebDriverTestCase {
	/** The minimum width and height of a pointer target, in CSS px. */
	private static final double MIN = 24;
	/** Sub-pixel layout can put touching boxes a fraction of a pixel apart. */
	private static final double EPSILON = 0.5;
	/** One closable tab per orientation and mold. */
	private static final String[] CLOSABLE_TABS = {
			"topClose", "bottomClose", "leftClose", "rightClose", "accordionClose"};

	private static final int LEFT = 0, TOP = 1, RIGHT = 2, BOTTOM = 3;

	@Test
	public void closeButtonsMeetTheMinimumInsideTheirTab() {
		connect();
		waitResponse();

		for (String tab : CLOSABLE_TABS) {
			double[] button = rectOf(tab, "cls"), owner = rectOf(tab, null);
			double width = button[RIGHT] - button[LEFT], height = button[BOTTOM] - button[TOP];
			Assertions.assertTrue(width >= MIN && height >= MIN,
					tab + ": the close button must be at least " + MIN + "px square, was " + width + "x" + height);
			Assertions.assertTrue(button[LEFT] >= owner[LEFT] - EPSILON && button[RIGHT] <= owner[RIGHT] + EPSILON
							&& button[TOP] >= owner[TOP] - EPSILON && button[BOTTOM] <= owner[BOTTOM] + EPSILON,
					tab + ": the close button must stay inside its tab, button " + Arrays.toString(button)
							+ " tab " + Arrays.toString(owner));
		}
	}

	@Test
	public void theCornerOfTheCloseButtonClosesTheTab() {
		connect();
		waitResponse();

		for (String tab : CLOSABLE_TABS) {
			getEval("zk.Widget.$('$" + tab + "').$n('cls').scrollIntoView({block: 'center'})");
			// 2px inside the top-left corner of a box of the minimum size
			int offset = (int) (MIN / 2) - 2;
			getActions().moveToElement(toElement(widget("$" + tab).$n("cls")), -offset, -offset)
					.click().perform();
			waitResponse();
			Assertions.assertFalse(jq("$" + tab).exists(),
					tab + ": a click near the corner of the close button must close the tab, not select it");
		}
	}

	@Test
	public void tabsOnTheRightKeepTheLabelAsClearAsTabsOnTheLeft() {
		connect();
		waitResponse();

		// the close button comes before the label of a tab on the left, and after it on the right
		double[] leftButton = rectOf("leftClose", "cls"), leftLabel = rectOf("leftClose", "cnt"),
				rightButton = rectOf("rightClose", "cls"), rightLabel = rectOf("rightClose", "cnt");
		double left = leftLabel[LEFT] - leftButton[RIGHT], right = rightButton[LEFT] - rightLabel[RIGHT];
		Assertions.assertTrue(right >= left - EPSILON, "a tab on the right must keep its label as clear of the close"
				+ " button as a tab on the left, " + right + "px against " + left + "px");
	}

	/** The bounding box of the tab's sub-node {@code subId}, or of the tab itself when null. */
	private static double[] rectOf(String tab, String subId) {
		String node = subId == null ? "$n()" : "$n('" + subId + "')";
		String rect = getEval("(function () { var r = zk.Widget.$('$" + tab + "')." + node
				+ ".getBoundingClientRect(); return [r.left, r.top, r.right, r.bottom].join(); })()");
		return Arrays.stream(rect.split(",")).mapToDouble(Double::parseDouble).toArray();
	}
}
