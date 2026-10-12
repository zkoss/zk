/* F110_ZK_6084_SliderTooltipTest.java

	Purpose:

	Description:

	History:
		Fri Sep 18 2026, Created by peggypeng

Copyright (C) 2026 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Duration;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.Keys;

import org.zkoss.test.webdriver.WebDriverTestCase;

/**
 * WCAG 1.4.13: a slider knob's value tooltip closes on Escape with the pointer still on the
 * knob, stays closed until the pointer leaves, and can be reached by moving onto it.
 */
@Tag("WcagTestOnly")
public class F110_ZK_6084_SliderTooltipTest extends WebDriverTestCase {
	private static final String KNOB = " .z-sliderbuttons-button";
	private static final String TOOLTIP = " .z-sliderbuttons-tooltip";
	private static final int STEP = 3;
	private static final Duration SETTLE = Duration.ofMillis(500);

	private void hoverKnob(String slider) {
		getActions().moveToElement(toElement(jq(slider + KNOB).first())).perform();
		waitResponse();
		assertTrue(jq(slider + TOOLTIP).first().isVisible(),
				"hovering the knob of " + slider + " should reveal its tooltip");
	}

	private void pressEscape() {
		getActions().sendKeys(Keys.ESCAPE).perform();
		waitResponse();
	}

	private void movePointerAway() {
		getActions().moveToElement(toElement(jq("$away"))).perform();
		waitResponse();
	}

	private double centre(String selector, String side, String extent) {
		return Double.parseDouble(getEval("(function(){var r=jq('" + selector
				+ "').first()[0].getBoundingClientRect();return r." + side + " + r." + extent + " / 2;})()"));
	}

	private String elementUnderPointer() {
		return getEval("(function(){var p=zk.currentPointer,"
				+ "e=document.elementFromPoint(p[0] - window.scrollX, p[1] - window.scrollY);"
				+ "return e ? e.className : 'nothing';})()");
	}

	private void walkFromKnobOntoTooltip(String slider) {
		String knob = slider + KNOB, tooltip = slider + TOOLTIP;
		hoverKnob(slider);

		int dx = (int) Math.round(centre(tooltip, "left", "width") - centre(knob, "left", "width"));
		int dy = (int) Math.round(centre(tooltip, "top", "height") - centre(knob, "top", "height"));
		int steps = Math.max(Math.max(Math.abs(dx), Math.abs(dy)) / STEP, 1);

		int movedX = 0, movedY = 0;
		for (int i = 1; i <= steps; i++) {
			int toX = dx * i / steps, toY = dy * i / steps;
			getActions().moveByOffset(toX - movedX, toY - movedY).perform();
			movedX = toX;
			movedY = toY;
			assertTrue(jq(tooltip).first().isVisible(),
					"the tooltip vanished " + i + "/" + steps + " of the way from the knob to it");
		}
		assertTrue(elementUnderPointer().contains("z-sliderbuttons-tooltip"),
				"the walk never reached the tooltip, it ended over: " + elementUnderPointer());
	}

	// The pointer survives page loads, so a test could start already on its target and never
	// make the hover transition it needs.
	private void connectAndParkPointer() {
		connect();
		waitResponse();
		movePointerAway();
	}

	@Test
	public void escapeHidesTheKnobTooltip() {
		connectAndParkPointer();
		hoverKnob("$hrs");

		pressEscape();

		assertFalse(jq("$hrs" + TOOLTIP).first().isVisible(),
				"Escape should hide the tooltip while the pointer is still on the knob");
	}

	@Test
	public void theTooltipStaysHiddenWhileTheKnobIsStillHovered() {
		connectAndParkPointer();
		hoverKnob("$hrs");
		pressEscape();
		assertFalse(jq("$hrs" + TOOLTIP).first().isVisible(), "precondition: dismissed");

		getActions().pause(SETTLE).perform();
		waitResponse();

		assertFalse(jq("$hrs" + TOOLTIP).first().isVisible(),
				"the dismissed tooltip should not reappear on its own while the knob is hovered");
	}

	@Test
	public void theTooltipReturnsAfterLeavingTheKnob() {
		connectAndParkPointer();
		hoverKnob("$hrs");
		pressEscape();
		assertFalse(jq("$hrs" + TOOLTIP).first().isVisible(), "precondition: dismissed");

		movePointerAway();
		hoverKnob("$hrs");
	}

	@Test
	public void theMultisliderTooltipDismissesToo() {
		connectAndParkPointer();
		hoverKnob("$ms");

		pressEscape();

		assertFalse(jq("$ms" + TOOLTIP).first().isVisible(),
				"the same fix serves Multislider, whose knob is the same Sliderbuttons");
	}

	@Test
	public void theVerticalTooltipDismissesToo() {
		connectAndParkPointer();
		hoverKnob("$vrs");

		pressEscape();

		assertFalse(jq("$vrs" + TOOLTIP).first().isVisible(),
				"a vertical slider puts its tooltip beside the knob, and it dismisses the same way");
	}

	@Test
	public void escapeLeavesAPinnedTooltipAlone() {
		connectAndParkPointer();
		assertTrue(jq("$pinned" + TOOLTIP).first().isVisible(), "precondition: tooltipVisible pins it");
		getActions().moveToElement(toElement(jq("$pinned" + KNOB).first())).perform();
		waitResponse();

		pressEscape();

		assertTrue(jq("$pinned" + TOOLTIP).first().isVisible(),
				"a pinned tooltip is not revealed by hover, so Escape must leave it alone");
	}

	@Test
	public void thePointerCanTravelFromTheKnobOntoTheTooltip() {
		connectAndParkPointer();

		walkFromKnobOntoTooltip("$hrs");
	}

	@Test
	public void thePointerCanTravelOntoTheVerticalTooltip() {
		connectAndParkPointer();

		walkFromKnobOntoTooltip("$vrs");
	}

	private void pressTheTooltipAndDrag(String slider, String log) {
		connectAndParkPointer();
		hoverKnob(slider);
		int left = jq(slider + KNOB).first().positionLeft();

		getActions().moveToElement(toElement(jq(slider + TOOLTIP).first()))
				.clickAndHold().moveByOffset(60, 0).release().perform();
		waitResponse();

		assertEquals(left, jq(slider + KNOB).first().positionLeft(),
				"the tooltip is a value readout, pressing it must not drag the knob");
		assertEquals("", jq(log).text(), "and nothing may reach the server");
	}

	@Test
	public void pressingTheTooltipStartsNoDrag() {
		pressTheTooltipAndDrag("$hrs", "$hrslog");
	}

	@Test
	public void pressingTheMultisliderTooltipStartsNoDragEither() {
		pressTheTooltipAndDrag("$ms", "$mslog");
	}

	@Test
	public void theTooltipDoesNotLookClickable() {
		connectAndParkPointer();
		hoverKnob("$hrs");

		assertEquals("default", jq("$hrs" + TOOLTIP).first().css("cursor"),
				"the slider sets cursor:pointer on every descendant, but the tooltip and its "
						+ "bridge must not advertise a click that is deliberately refused");
	}
}
