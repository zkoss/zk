/* B110_ZK_6132Test.java

	Purpose:

	Description:

	History:
		Mon Aug 31 11:55:41 CST 2026, Created by peakerlee

Copyright (C) 2026 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;

public class B110_ZK_6132Test extends WebDriverTestCase {

	private static final String PAGE = "/test2/B110-ZK-6132.zul";
	private static final String REJECTED = "danger=REJECTED secondary=REJECTED severity=error";
	private static final String ACCEPTED = "error=ACCEPTED neutral=ACCEPTED severity=neutral";

	@Test
	public void renamed_tokens_reach_the_severity_class() {
		connect(PAGE);
		waitResponse();
		assertTrue(jq("$chipError").hasClass("z-chip-error"));
		assertTrue(jq("$chipNeutral").hasClass("z-chip-neutral"));
		assertTrue(jq("$badgeError").hasClass("z-badge-error"));
		assertTrue(jq("$badgeNeutral").hasClass("z-badge-neutral"));
	}

	// One popup per connect: an open popup covers the neighbouring trigger.
	@Test
	public void confirmpopup_error_reaches_the_severity_class() {
		connect(PAGE);
		waitResponse();
		click(jq("$btnOpenError"));
		waitResponse();
		assertTrue(jq("$cpError").hasClass("z-confirmpopup-error"));
	}

	@Test
	public void confirmpopup_neutral_reaches_the_severity_class() {
		connect(PAGE);
		waitResponse();
		click(jq("$btnOpenNeutral"));
		waitResponse();
		assertTrue(jq("$cpNeutral").hasClass("z-confirmpopup-neutral"));
	}

	@Test
	public void chip_rejects_the_retired_tokens() {
		connect(PAGE);
		waitResponse();
		click(jq("$btnChipOld"));
		waitResponse();
		assertEquals(REJECTED, jq("$chipResult").text(),
				"Chip#setSeverity must throw for danger/secondary — no deprecated alias");
	}

	@Test
	public void badge_rejects_the_retired_tokens() {
		connect(PAGE);
		waitResponse();
		click(jq("$btnBadgeOld"));
		waitResponse();
		assertEquals(REJECTED, jq("$badgeResult").text(),
				"Badge#setSeverity must throw for danger/secondary — no deprecated alias");
	}

	@Test
	public void confirmpopup_rejects_the_retired_tokens() {
		connect(PAGE);
		waitResponse();
		click(jq("$btnPopupOld"));
		waitResponse();
		assertEquals(REJECTED, jq("$popupResult").text(),
				"Confirmpopup#setSeverity must throw for danger/secondary — no deprecated alias");
	}

	@Test
	public void all_three_accept_the_renamed_tokens() {
		connect(PAGE);
		waitResponse();
		click(jq("$btnChipNew"));
		waitResponse();
		click(jq("$btnBadgeNew"));
		waitResponse();
		click(jq("$btnPopupNew"));
		waitResponse();
		assertEquals(ACCEPTED, jq("$chipResult").text());
		assertEquals(ACCEPTED, jq("$badgeResult").text());
		assertEquals(ACCEPTED, jq("$popupResult").text());
		// the last accepted value must also reach the DOM, not just the getter
		assertTrue(jq("$chipError").hasClass("z-chip-neutral"));
		assertFalse(jq("$chipError").hasClass("z-chip-error"));
	}

}
