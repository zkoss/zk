/* F104_ZK_6097_BreadcrumbMVCTest.java

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

public class F104_ZK_6097_BreadcrumbMVCTest extends WebDriverTestCase {

	@Override
	protected String getFileLocation() {
		return "/test2/F104-ZK-6097-Breadcrumb-MVC.zul";
	}

	// @Wire Breadcrumb + programmatic setSeparator
	@Test
	public void mvc_wire_breadcrumb_set_separator() {
		connect();
		waitResponse();
		assertEquals("/", jq("$mvcSeparator").text(), "initial separator = /");

		click(jq("$btnPipeMvc"));
		waitResponse();
		assertEquals("|", jq("$mvcSeparator").text(),
				"Composer setSeparator('|') must update separator and label");
		String domText = jq("$bcMvc").text();
		assertTrue(domText.contains("|"),
				"breadcrumb DOM must show | separator after Composer setSeparator");
	}

	// @Wire Breadcrumb + programmatic appendChild (add Breadcrumbitem)
	@Test
	public void mvc_wire_breadcrumb_add_item() {
		connect();
		waitResponse();
		int before = jq("$bcMvc .z-breadcrumbitem").length();
		assertEquals(3, before, "initial breadcrumb has 3 items");

		click(jq("$btnAddItem"));
		waitResponse();
		int after = jq("$bcMvc .z-breadcrumbitem").length();
		assertEquals(4, after,
				"Composer appendChild(new Breadcrumbitem('New Page')) must add a 4th item");
		String domText = jq("$bcMvc").text();
		assertTrue(domText.contains("New Page"),
				"newly added item label 'New Page' must appear in DOM");
	}

	// @Wire Breadcrumb + programmatic setMaxItems triggers collapse
	@Test
	public void mvc_wire_breadcrumb_set_maxItems_triggers_collapse() {
		connect();
		waitResponse();
		// Initial 3 items, maxItems=0 — no ellipsis
		assertFalse(jq("$bcMvc .z-breadcrumb-ellipsis").exists(),
				"initial maxItems=0 must not show ellipsis");

		// Add 2 more items to get 5 total (need > maxItems to trigger collapse)
		click(jq("$btnAddItem"));
		waitResponse();
		click(jq("$btnAddItem"));
		waitResponse();
		// Now 5 items, set maxItems=3
		click(jq("$btnSetMaxItems"));
		waitResponse();
		assertTrue(jq("$bcMvc .z-breadcrumb-ellipsis").exists(),
				"Composer setMaxItems(3) with 5 items must trigger ellipsis collapse");
	}
}
