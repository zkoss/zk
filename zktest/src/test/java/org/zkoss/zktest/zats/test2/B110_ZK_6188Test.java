/* B110_ZK_6188Test.java

		Purpose:

		Description:

		History:
				Mon Oct 12 10:00:00 CST 2026, Created by peakerlee

Copyright (C) 2026 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.zkoss.test.webdriver.WebDriverTestCase;

public class B110_ZK_6188Test extends WebDriverTestCase {

	private static final String BOX = "jq('.z-errorbox')[0]";

	private static final String CONTENT = "jq('.z-errorbox-content')[0]";

	private static final String POINTER = "jq('.z-errorbox-pointer')[0]";

	private static final String FIELD = "jq('$tb')[0]";

	@Test
	public void testDragAboveField() {
		openErrorbox();

		drag(rect(FIELD, "left") - rect(BOX, "left"), -(rect(BOX, "height") + 30));
		assertTrue(pointerHasClass("z-errorbox-down"), "precondition: the box was dragged above the field");

		double pointerY = (rect(POINTER, "top") + rect(POINTER, "bottom")) / 2;
		double contentBottom = rect(CONTENT, "bottom");
		double fieldTop = rect(FIELD, "top");
		assertAll(
				() -> assertEquals("", getEval(POINTER + ".style.top"),
						"the left direction's top must be cleared"),
				() -> assertTrue(pointerY >= contentBottom - 1 && pointerY <= fieldTop,
						"the pointer must sit on the box's bottom edge, between the box and the field: pointer y "
								+ pointerY + ", box bottom " + contentBottom + ", field top " + fieldTop));
	}

	@Test
	public void testDragLeftOfField() {
		openErrorbox();

		drag(rect(FIELD, "left") - rect(BOX, "right") - 30, 0);
		assertTrue(pointerHasClass("z-errorbox-right"), "precondition: the box was dragged left of the field");

		double pointerX = (rect(POINTER, "left") + rect(POINTER, "right")) / 2;
		double contentRight = rect(CONTENT, "right");
		double fieldLeft = rect(FIELD, "left");
		assertAll(
				() -> assertEquals("", getEval(POINTER + ".style.left"),
						"the left direction's left must be cleared"),
				() -> assertTrue(pointerX >= contentRight - 1 && pointerX <= fieldLeft,
						"the pointer must sit on the box's right edge, between the box and the field: pointer x "
								+ pointerX + ", box right " + contentRight + ", field left " + fieldLeft));
	}

	@Test
	public void testCornerBelowToAboveLeftOfField() {
		openErrorbox();

		drag(rect(FIELD, "left") - rect(BOX, "right") - 30, rect(FIELD, "bottom") - rect(BOX, "top") + 30);
		assertTrue(pointerHasClass("z-errorbox-up"), "precondition: the box was dragged below-left of the field");

		drag(0, rect(FIELD, "top") - rect(BOX, "bottom") - 30);
		assertTrue(pointerHasClass("z-errorbox-down"), "precondition: the box was dragged above-left of the field");

		double pointerY = (rect(POINTER, "top") + rect(POINTER, "bottom")) / 2;
		double contentBottom = rect(CONTENT, "bottom");
		double fieldTop = rect(FIELD, "top");
		assertAll(
				() -> assertEquals("", getEval(POINTER + ".style.top"),
						"the up-corner direction's top must be cleared"),
				() -> assertTrue(pointerY >= contentBottom - 1 && pointerY <= fieldTop,
						"the pointer must sit on the box's bottom edge, between the box and the field: pointer y "
								+ pointerY + ", box bottom " + contentBottom + ", field top " + fieldTop));
	}

	private void openErrorbox() {
		connect();
		focus(jq("$tb"));
		blur(jq("$tb"));
		waitResponse();
		assertTrue(jq(".z-errorbox").isVisible(), "precondition: the errorbox is open");
		assertTrue(pointerHasClass("z-errorbox-left"), "precondition: the errorbox opens right of the field");
	}

	private void drag(double dx, double dy) {
		getActions().moveToElement(toElement(jq(".z-errorbox-content")))
				.clickAndHold()
				.moveByOffset((int) Math.round(dx), (int) Math.round(dy))
				.release()
				.perform();
		waitResponse();
	}

	private boolean pointerHasClass(String sclass) {
		return Boolean.parseBoolean(getEval(POINTER + ".classList.contains('" + sclass + "')"));
	}

	private double rect(String node, String side) {
		return Double.parseDouble(getEval(node + ".getBoundingClientRect()." + side));
	}
}
