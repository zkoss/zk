/* F110_ZK_6085_ChosenboxRemoveTargetSizeTest.java

	Purpose:
		WCAG 2.5.8 Target Size (Minimum) for the remove button on a Chosenbox item.

	Description:
		The remove button sits inside its item, and a click anywhere else in the
		item focuses the input and opens the list, so the remove button has to
		meet the minimum size on its own. za11y enlarges it in
		za11y/less/iceblue.less, and a disabled Chosenbox still hides it.

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
public class F110_ZK_6085_ChosenboxRemoveTargetSizeTest extends WebDriverTestCase {
	/** The minimum width and height of a pointer target, in CSS px. */
	private static final double MIN = 24;
	/** Sub-pixel layout can put touching boxes a fraction of a pixel apart. */
	private static final double EPSILON = 0.5;
	private static final String ITEM = ".z-chosenbox-item";
	private static final String REMOVE = ".z-chosenbox-delete";
	private static final String LABEL = ".z-chosenbox-item-content";

	private static final int LEFT = 0, TOP = 1, RIGHT = 2, BOTTOM = 3;

	@Test
	public void removeButtonsMeetTheMinimumInsideTheirItem() {
		connect();
		waitResponse();

		assertRemoveButtonsMeetTheMinimum("fruits");
	}

	@Test
	public void removeButtonsOfWrappedItemsStayInsideTheirItem() {
		connect();
		waitResponse();

		double[] first = rectOf("wrapped", 0, null), last = rectOf("wrapped", 2, null);
		Assertions.assertTrue(last[TOP] >= first[BOTTOM] - EPSILON,
				"precondition: the items of the narrow chosenbox wrap onto another row");
		assertRemoveButtonsMeetTheMinimum("wrapped");
	}

	@Test
	public void theCornerOfTheRemoveButtonRemovesTheItem() {
		connect();
		waitResponse();

		String items = "$fruits " + ITEM;
		// 2px inside the top-left corner of a box of the minimum size
		int offset = (int) (MIN / 2) - 2;
		for (int left = 3; left > 0; left--) {
			Assertions.assertEquals(left, jq(items).length(), "precondition: " + left + " selected items");
			getActions().moveToElement(toElement(jq(items).eq(0).find(REMOVE)), -offset, -offset)
					.click().perform();
			waitResponse();
			Assertions.assertEquals(left - 1, jq(items).length(),
					"a click near the corner of the remove button must remove the item, not open the list");
		}
	}

	@Test
	public void aDisabledChosenboxShowsNoRemoveButton() {
		connect();
		waitResponse();

		Assertions.assertEquals(3, jq("$disabled " + ITEM).length(), "precondition: three selected items");
		Assertions.assertFalse(jq("$disabled " + REMOVE).isVisible(),
				"a disabled chosenbox must not show remove buttons, as it does not act on them");
	}

	private void assertRemoveButtonsMeetTheMinimum(String id) {
		int items = jq("$" + id + " " + ITEM).length();
		Assertions.assertEquals(3, items, "precondition: three selected items");
		double[] clip = paddingBoxOf(id);
		for (int i = 0; i < items; i++) {
			double[] button = rectOf(id, i, REMOVE), item = rectOf(id, i, null), label = rectOf(id, i, LABEL);
			double width = button[RIGHT] - button[LEFT], height = button[BOTTOM] - button[TOP];
			String name = id + " item " + i;
			Assertions.assertTrue(width >= MIN && height >= MIN,
					name + ": the remove button must be at least " + MIN + "px square, was " + width + "x" + height);
			Assertions.assertTrue(inside(button, item),
					name + ": the remove button must stay inside its item, button " + Arrays.toString(button)
							+ " item " + Arrays.toString(item));
			Assertions.assertTrue(inside(button, clip),
					name + ": the remove button must lie inside the chosenbox, which clips it, button "
							+ Arrays.toString(button) + " chosenbox " + Arrays.toString(clip));
			Assertions.assertTrue(label[RIGHT] <= button[LEFT] + EPSILON,
					name + ": the label must not run under the remove button, label ends at " + label[RIGHT]
							+ " and the button starts at " + button[LEFT]);
		}
	}

	private static boolean inside(double[] r, double[] box) {
		return r[LEFT] >= box[LEFT] - EPSILON && r[TOP] >= box[TOP] - EPSILON
				&& r[RIGHT] <= box[RIGHT] + EPSILON && r[BOTTOM] <= box[BOTTOM] + EPSILON;
	}

	/** The bounding box of {@code sub} inside the {@code index}-th item of a chosenbox, or of the item itself when null. */
	private static double[] rectOf(String id, int index, String sub) {
		String node = "jq('$" + id + " " + ITEM + "').eq(" + index + ")" + (sub == null ? "" : ".find('" + sub + "')");
		return toDoubles(getEval("(function () { var r = " + node + "[0].getBoundingClientRect();"
				+ " return [r.left, r.top, r.right, r.bottom].join(); })()"));
	}

	/** The box inside the border, which is what {@code overflow: hidden} clips to. */
	private static double[] paddingBoxOf(String id) {
		return toDoubles(getEval("(function () { var n = jq('$" + id + "')[0],"
				+ " r = n.getBoundingClientRect(), s = getComputedStyle(n);"
				+ " return [r.left + parseFloat(s.borderLeftWidth), r.top + parseFloat(s.borderTopWidth),"
				+ " r.right - parseFloat(s.borderRightWidth), r.bottom - parseFloat(s.borderBottomWidth)].join(); })()"));
	}

	private static double[] toDoubles(String csv) {
		return Arrays.stream(csv.split(",")).mapToDouble(Double::parseDouble).toArray();
	}
}
