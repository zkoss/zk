/* B110_ZK_6164Test.java

        Purpose:

        Description:

        History:
                Thu Sep 24 15:18:55 CST 2026, Created by peakerlee

Copyright (C) 2026 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

public class B110_ZK_6164Test extends WebDriverTestCase {

	private void openPopup() {
		click(jq("$opener"));
		waitResponse();
	}

	private String styleOf(String selector, String prop) {
		return jq(selector).css(prop);
	}

	/** Moves the token, then reads it back off the node. Comparing the popup's
	 *  computed value against the plain button's cannot tell a wired-up token
	 *  from a literal that happens to match today; moving it can. */
	private void overrideToken(String name, String value) {
		eval("document.documentElement.style.setProperty('" + name + "', '" + value + "')");
	}

	@Test
	public void testButtonsFollowTheButtonTokens() {
		connect();
		waitResponse();
		openPopup();

		overrideToken("--zk-button-padding", "13px 29px");
		overrideToken("--zk-button-border-radius", "7px");
		overrideToken("--zk-button-background-color", "rgb(1, 2, 3)");

		for (String sel : new String[] {".z-confirmpopup-ok", ".z-confirmpopup-cancel"}) {
			assertEquals("13px 29px", styleOf(sel, "padding"),
					sel + " must take its padding from --zk-button-padding");
			assertEquals("7px", styleOf(sel, "border-radius"),
					sel + " must take its radius from --zk-button-border-radius");
		}
		// OK holds focus (defaultFocus="ok"), so only Cancel shows the resting fill.
		assertEquals("rgb(1, 2, 3)", styleOf(".z-confirmpopup-cancel", "background-color"),
				"Cancel must take its fill from --zk-button-background-color");
	}

	/** OK takes focus on every open; a focused .z-button reads --zk-button-focus-*. */
	@Test
	public void testFocusedButtonFollowsTheFocusTokens() {
		connect();
		waitResponse();
		openPopup();
		// give the setTimeout(0) focus dispatch a tick to land
		sleep(80);
		assertTrue(Boolean.parseBoolean(getEval(
				"jq('.z-confirmpopup-ok')[0] === document.activeElement")), "OK must hold focus");

		overrideToken("--zk-button-focus-color", "rgb(10, 11, 12)");
		overrideToken("--zk-button-focus-border-color", "rgb(13, 14, 15)");
		overrideToken("--zk-button-focus-background-color", "rgb(16, 17, 18)");

		assertEquals("rgb(10, 11, 12)", styleOf(".z-confirmpopup-ok", "color"),
				"focused OK must take its text colour from --zk-button-focus-color");
		assertEquals("rgb(13, 14, 15)", styleOf(".z-confirmpopup-ok", "border-color"),
				"focused OK must take its border from --zk-button-focus-border-color");
		assertEquals("rgb(16, 17, 18)", styleOf(".z-confirmpopup-ok", "background-color"),
				"focused OK must take its fill from --zk-button-focus-background-color");
	}

	/** Same tokens in, same geometry out as a plain button on the page. */
	@Test
	public void testButtonsMatchThePlainButtonGeometry() {
		connect();
		waitResponse();
		openPopup();

		String plainPadding = styleOf("$plain", "padding");
		String plainMinHeight = styleOf("$plain", "min-height");
		String plainRadius = styleOf("$plain", "border-radius");

		for (String sel : new String[] {".z-confirmpopup-ok", ".z-confirmpopup-cancel"}) {
			assertEquals(plainPadding, styleOf(sel, "padding"), sel + " padding must match .z-button");
			assertEquals(plainMinHeight, styleOf(sel, "min-height"), sel + " min-height must match .z-button");
			assertEquals(plainRadius, styleOf(sel, "border-radius"), sel + " radius must match .z-button");
		}
	}

	/** The shadow is what makes it read as a button at a glance. */
	@Test
	public void testButtonsCarryTheButtonShadow() {
		connect();
		waitResponse();
		openPopup();

		String plain = styleOf("$plain", "box-shadow");
		for (String sel : new String[] {".z-confirmpopup-ok", ".z-confirmpopup-cancel"}) {
			assertEquals(plain, styleOf(sel, "box-shadow"), sel + " must carry the button shadow");
		}
	}

	/** Hover and press are button states too; before the fix neither had a rule. */
	@Test
	public void testButtonsFollowTheHoverAndActiveTokens() {
		connect();
		waitResponse();
		openPopup();

		overrideToken("--zk-button-hover-background-color", "rgb(4, 5, 6)");
		overrideToken("--zk-button-active-background-color", "rgb(7, 8, 9)");

		JQuery cancel = jq(".z-confirmpopup-cancel");
		mouseOver(cancel);
		assertEquals("rgb(4, 5, 6)", cancel.css("background-color"),
				"hover must take its fill from --zk-button-hover-background-color");

		getActions().clickAndHold(toElement(cancel)).perform();
		try {
			assertEquals("rgb(7, 8, 9)", cancel.css("background-color"),
					"press must take its fill from --zk-button-active-background-color");
		} finally {
			getActions().release().perform();
			waitResponse();
		}
	}
}
