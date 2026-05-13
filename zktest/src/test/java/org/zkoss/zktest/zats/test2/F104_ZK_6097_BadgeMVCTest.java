/* F104_ZK_6097_BadgeMVCTest.java

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

public class F104_ZK_6097_BadgeMVCTest extends WebDriverTestCase {

	@Override
	protected String getFileLocation() {
		return "/test2/F104-ZK-6097-Badge-MVC.zul";
	}

	// @Wire Badge + programmatic setCount via Composer
	@Test
	public void mvc_wire_badge_count_increment() {
		connect();
		waitResponse();
		assertEquals("0", jq("$mvcCount").text(), "initial count = 0");

		click(jq("$btnIncrementMvc"));
		waitResponse();
		assertEquals("1", jq("$mvcCount").text(),
				"Composer setCount(+1) must update count and label");

		click(jq("$btnIncrementMvc"));
		waitResponse();
		assertEquals("2", jq("$mvcCount").text(), "second increment must reach 2");
	}

	// reset via Composer
	@Test
	public void mvc_wire_badge_reset_count() {
		connect();
		waitResponse();
		click(jq("$btnIncrementMvc"));
		waitResponse();
		click(jq("$btnIncrementMvc"));
		waitResponse();
		assertEquals("2", jq("$mvcCount").text());

		click(jq("$btnResetMvc"));
		waitResponse();
		assertEquals("0", jq("$mvcCount").text(),
				"Composer reset must set count to 0");
	}

	// setSeverity via Composer changes CSS class
	@Test
	public void mvc_wire_badge_set_severity() {
		connect();
		waitResponse();
		assertFalse(jq("$bdgMvc").hasClass("z-badge-danger"), "initial not danger");

		click(jq("$btnSetDangerMvc"));
		waitResponse();
		assertTrue(jq("$bdgMvc").hasClass("z-badge-danger"),
				"Composer setSeverity('danger') must apply z-badge-danger class");
	}
}
