/* F104_ZK_6097_BreadcrumbMVVMTest.java

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

public class F104_ZK_6097_BreadcrumbMVVMTest extends WebDriverTestCase {

	@Override
	protected String getFileLocation() {
		return "/test2/F104-ZK-6097-Breadcrumb-MVVM.zul";
	}

	// separator @load binding: initial "/" then @command changes to "|"
	@Test
	public void mvvm_separator_load_binding_changes() {
		connect();
		waitResponse();
		assertEquals("/", jq("$mvvmSeparator").text(), "initial separator = /");

		click(jq("$btnPipe"));
		waitResponse();
		assertEquals("|", jq("$mvvmSeparator").text(),
				"@command usePipeSeparator must update separator via @NotifyChange");
		// The breadcrumb separator in DOM should now be "|"
		String domText = jq("$bcMvvm").text();
		assertTrue(domText.contains("|"),
				"@load(vm.separator) must update separator in breadcrumb DOM, text: " + domText);
	}

	// @command restores default separator
	@Test
	public void mvvm_separator_restore_to_default() {
		connect();
		waitResponse();
		click(jq("$btnPipe"));
		waitResponse();
		assertEquals("|", jq("$mvvmSeparator").text());

		click(jq("$btnSlash"));
		waitResponse();
		assertEquals("/", jq("$mvvmSeparator").text(),
				"@command useDefaultSeparator must restore separator to /");
	}

	// maxItems @load binding: 0 = no collapse; 3 = collapse
	@Test
	public void mvvm_maxItems_load_binding_enables_collapse() {
		connect();
		waitResponse();
		assertEquals("0", jq("$mvvmMaxItems").text(), "initial maxItems = 0");
		// With maxItems=0, no ellipsis should exist
		assertFalse(jq("$bcMvvm .z-breadcrumb-ellipsis").exists(),
				"maxItems=0 must not show ellipsis");

		click(jq("$btnCollapse"));
		waitResponse();
		assertEquals("3", jq("$mvvmMaxItems").text(),
				"@command enableCollapse must set maxItems=3");
		// With maxItems=3 and 5 items, ellipsis should appear
		assertTrue(jq("$bcMvvm .z-breadcrumb-ellipsis").exists(),
				"maxItems=3 with 5 items must show ellipsis");
	}
}
