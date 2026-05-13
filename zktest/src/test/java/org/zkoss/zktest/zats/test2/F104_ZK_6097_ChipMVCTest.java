/* F104_ZK_6097_ChipMVCTest.java

	Purpose:

	Description:

	History:
		Wed May 13 13:05:00 CST 2026, Created by peakerlee

Copyright (C) 2026 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;

public class F104_ZK_6097_ChipMVCTest extends WebDriverTestCase {

	@Override
	protected String getFileLocation() {
		return "/test2/F104-ZK-6097-Chip-MVC.zul";
	}

	// onClose event routed to Composer @Listen
	@Test
	public void mvc_onClose_routed_to_composer_listener() {
		connect();
		waitResponse();
		assertEquals("open", jq("$mvcResult").text(), "initial state");

		click(jq("$chipMvc .z-chip-close"));
		waitResponse();
		assertEquals("closed", jq("$mvcResult").text(),
				"@Listen(onClose=#chipMvc) in Composer must receive the event and update label");
	}

	// @Wire Chip + programmatic setSeverity from Composer button
	@Test
	public void mvc_wire_chip_programmatic_severity_change() {
		connect();
		waitResponse();
		assertTrue(jq("$chipMvc").hasClass("z-chip-warning"),
				"initial severity='warning' must be reflected");

		click(jq("$btnChangeSeverity"));
		waitResponse();
		assertTrue(jq("$chipMvc").hasClass("z-chip-success"),
				"Composer setSeverity('success') must update chip CSS class");
		assertFalse(jq("$chipMvc").hasClass("z-chip-warning"),
				"old severity class must be removed after Composer update");
	}

	// @Wire Chip + programmatic setLabel from Composer
	@Test
	public void mvc_wire_chip_programmatic_label_change() {
		connect();
		waitResponse();
		assertEquals("MVC Chip", jq("$mvcLabel").text());

		click(jq("$btnChangeLabel"));
		waitResponse();
		assertEquals("Changed by Composer", jq("$mvcLabel").text(),
				"Composer setLabel must update chip label and the tracking label");
		assertEquals("Changed by Composer", jq("$chipMvc").text().trim(),
				"chip DOM must reflect the new label");
	}
}
