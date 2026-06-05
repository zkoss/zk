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

		// The trigger's accessible name is layered on by the za11y add-on from
		// msgza11y.SPEEDDIAL (default "Speed dial"); assert the actual value, not
		// mere presence. Guard on za11y presence (CLAUDE.md a11y rule #1) so the
		// assertion is skipped in a NO_A11Y run where the augment is absent.
		if (Boolean.valueOf(getEval("!!window.za11y"))) {
			String ariaLabel = jq("$dial").find("button").first().attr("aria-label");
			Assertions.assertEquals("Speed dial", ariaLabel,
					"trigger aria-label must be the za11y default (msgza11y.SPEEDDIAL)");
		}
		Assertions.assertEquals("menu",
				jq("$dial").find("button").first().attr("aria-haspopup"));
		Assertions.assertEquals("menu",
				jq("$dial").find("ul").first().attr("role"));
	}
}
