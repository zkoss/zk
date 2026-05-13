/* F104_ZK_6097_AvatarMVCTest.java

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

public class F104_ZK_6097_AvatarMVCTest extends WebDriverTestCase {

	@Override
	protected String getFileLocation() {
		return "/test2/F104-ZK-6097-Avatar-MVC.zul";
	}

	// @Wire Avatar + programmatic setShape from Composer
	@Test
	public void mvc_wire_avatar_set_shape() {
		connect();
		waitResponse();
		assertEquals("circle", jq("$mvcShape").text(), "initial shape = circle");
		assertFalse(jq("$avMvc").hasClass("z-avatar-square"));

		click(jq("$btnSquare"));
		waitResponse();
		assertEquals("square", jq("$mvcShape").text(),
				"Composer setShape('square') must update shape and tracking label");
		assertTrue(jq("$avMvc").hasClass("z-avatar-square"),
				"Composer setShape must apply z-avatar-square CSS class");
	}

	// @Wire Avatar + programmatic setSize
	@Test
	public void mvc_wire_avatar_set_size() {
		connect();
		waitResponse();
		assertFalse(jq("$avMvc").hasClass("z-avatar-large"), "initial size != large");

		click(jq("$btnLarge"));
		waitResponse();
		assertTrue(jq("$avMvc").hasClass("z-avatar-large"),
				"Composer setSize('large') must apply z-avatar-large class");
	}

	// @Wire Avatar + programmatic setLabel
	@Test
	public void mvc_wire_avatar_set_label() {
		connect();
		waitResponse();
		assertEquals("AB", jq("$mvcLabel").text(), "initial label = AB");

		click(jq("$btnChangeLabel"));
		waitResponse();
		assertEquals("XY", jq("$mvcLabel").text(),
				"Composer setLabel('XY') must update label and tracking label");
		assertTrue(jq("$avMvc").text().contains("XY"),
				"avatar DOM must reflect new label text");
	}
}
