/* SpeeddialTest.java

	Purpose:

	Description:

	History:
		Thu May 14 16:06:11 CST 2026, Created by peakerlee

Copyright (C) 2026 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.wcag;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.Keys;

public class SpeeddialTest extends WcagTestCase {
	@Test
	public void test() {
		connect();
		verifyA11y();

		click(jq("$dial").find("button").first());
		waitResponse(true);
		verifyA11y();
	}

	// WCAG 2.1 SC 2.1.4 Character Key Shortcuts — Esc on the open dial trigger closes it.
	@Test
	public void testEscapeClosesDial() {
		connect();

		click(jq("$dial").find("button").first());
		waitResponse(true);
		Assertions.assertEquals("true",
				jq("$dial").find("button").first().attr("aria-expanded"),
				"dial should be open after trigger click");

		sendKeys(jq("$dial").find("button").first(), Keys.ESCAPE);
		waitResponse(true);
		Assertions.assertEquals("false",
				jq("$dial").find("button").first().attr("aria-expanded"),
				"Esc should close the dial");
	}

	// WCAG 2.1 SC 2.5.3 Label in Name + SC 4.1.2 Name/Role/Value — programmatic name and role.
	@Test
	public void testNameRoleValue() {
		connect();

		// Relocating ca:aria-label from the role-less wrapper to the trigger is
		// done by getA11yRealNode_, which lives in za11y — so both the override
		// and the localized fallback are za11y-only. Guard the pair on za11y
		// presence (CLAUDE.md a11y rule #1) or the NO_A11Y variant goes red.
		if (Boolean.valueOf(getEval("!!window.za11y"))) {
			Assertions.assertEquals("Quick actions",
					jq("$dial").find("button").first().attr("aria-label"),
					"ca:aria-label must name the trigger, not the role-less wrapper");
			Assertions.assertEquals("Speed dial",
					jq("$defaultNameDial").find("button").first().attr("aria-label"),
					"an unnamed trigger must fall back to the za11y default (msgza11y.SPEEDDIAL)");
		}
		Assertions.assertEquals("menu",
				jq("$dial").find("button").first().attr("aria-haspopup"));
		Assertions.assertEquals("menu",
				jq("$dial").find("ul").first().attr("role"));
	}
}
