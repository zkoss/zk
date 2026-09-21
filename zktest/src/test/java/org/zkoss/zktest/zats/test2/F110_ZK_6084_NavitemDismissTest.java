/* F110_ZK_6084_NavitemDismissTest.java

	Purpose:

	Description:

	History:
		Fri Sep 18 2026, Created by peggypeng

Copyright (C) 2026 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Duration;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.Keys;

import org.zkoss.test.webdriver.WebDriverTestCase;

/**
 * WCAG 1.4.13 Dismissible: a collapsed Navbar shows icons only, so hovering an item reveals its
 * label beside it and hovering a nav reveals its children. Both have to go away on Escape with
 * the pointer still on the icon. za11y only - hovering focuses nothing, so CE's Escape handling,
 * which hangs off the focused widget's doKeyDown_, never sees it.
 *
 * <p>Each reveal adds a class that lasts only while it is up, so its presence is the assertion.
 */
@Tag("WcagTestOnly")
public class F110_ZK_6084_NavitemDismissTest extends WebDriverTestCase {
	private static final String ITEM_LABEL = ".z-navitem-text-popup";
	private static final String NAV_POPUP = ".z-nav-popup";
	/** Nothing is known to re-reveal the label, so this is the window a regression would show in. */
	private static final Duration SETTLE = Duration.ofMillis(500);

	private void hoverLeafItem() {
		getActions().moveToElement(toElement(jq("$leafItem"))).perform();
		waitResponse();
		assertTrue(jq(ITEM_LABEL).isVisible(), "hovering a collapsed navitem should reveal its label");
	}

	/** Sends Escape without touching the pointer, so the item stays hovered. */
	private void pressEscape() {
		getActions().sendKeys(Keys.ESCAPE).perform();
		waitResponse();
	}

	private void movePointerAway() {
		getActions().moveToElement(toElement(jq("$away"))).perform();
		waitResponse();
	}

	/**
	 * The virtual pointer survives page loads, so a test can otherwise start with the pointer
	 * already on its target, and the hover transition it needs never happens.
	 */
	private void connectAndParkPointer() {
		connect();
		waitResponse();
		movePointerAway();
	}

	@Test
	public void escapeHidesTheLabelRevealedByHover() {
		connectAndParkPointer();
		hoverLeafItem();

		pressEscape();

		assertFalse(jq(ITEM_LABEL).exists(),
				"Escape should hide the label while the pointer is still on the item");
	}

	@Test
	public void theLabelStaysHiddenWhileTheItemIsStillHovered() {
		connectAndParkPointer();
		hoverLeafItem();
		pressEscape();
		assertFalse(jq(ITEM_LABEL).exists(), "precondition: dismissed");

		getActions().pause(SETTLE).perform();
		waitResponse();

		assertFalse(jq(ITEM_LABEL).exists(),
				"the dismissed label should not reappear on its own while the item is hovered");
	}

	/** 1.4.13 asks for dismissible, not permanently dismissed. */
	@Test
	public void theLabelReappearsAfterLeavingAndReturning() {
		connectAndParkPointer();
		hoverLeafItem();
		pressEscape();
		assertFalse(jq(ITEM_LABEL).exists(), "precondition: dismissed");

		movePointerAway();
		hoverLeafItem();
	}

	/**
	 * A nav's child list is hover-revealed content too, but dismissible for a different reason:
	 * za11y moves focus into the nav as it opens, so ZK's own Escape handling reaches it. Nothing
	 * here added that, so this is the case that catches it silently going away.
	 */
	@Test
	public void escapeAlsoClosesTheHoverOpenedNavPopup() {
		connectAndParkPointer();
		getActions().moveToElement(toElement(jq("$navWithChildren"))).perform();
		waitResponse();
		assertTrue(jq(NAV_POPUP).isVisible(), "hovering a collapsed nav should open its child list");

		pressEscape();

		assertFalse(jq(NAV_POPUP).exists(),
				"Escape should close the child list while the pointer is still on the nav");
	}
}
