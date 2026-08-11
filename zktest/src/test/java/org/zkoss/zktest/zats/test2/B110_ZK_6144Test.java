/* B110_ZK_6144Test.java

        Purpose:

        Description:

        History:
                Tue Aug 11 10:20:31 CST 2026, Created by peakerlee

Copyright (C) 2026 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.Dimension;

import org.zkoss.test.webdriver.WebDriverTestCase;

public class B110_ZK_6144Test extends WebDriverTestCase {
	private static final int NARROW = 600; // by default, sm range (576..767) -> stacking

	@Test
	public void testNestedGridKeepsTableRendering() {
		connect();
		waitResponse();
		int h = driver.manage().window().getSize().height;
		driver.manage().window().setSize(new Dimension(NARROW, h));
		waitResponse();
		assertTrue(jq("$outer").hasClass("z-grid--stacking"));
		assertFalse(jq("$inner").hasClass("z-grid--stacking"));

		assertEquals("grid", displayOf("outer", ":scope > .z-grid-body > table > tbody"),
				"outer grid should lay its rows out as cards");
		assertEquals("table", displayOf("inner", ":scope > .z-grid-body > table"),
				"nested grid should stay a table");
		assertEquals("table-row-group", displayOf("inner", ":scope > .z-grid-body > table > tbody"),
				"nested grid body should stay a row group");
		assertEquals("table-row", displayOf("inner", ":scope > .z-grid-body > table > tbody > tr"),
				"nested grid rows should not become cards");
		assertEquals("table-cell", displayOf("inner", ":scope > .z-grid-body > table > tbody > tr > td"),
				"nested grid cells should not become key-value blocks");
	}

	private String displayOf(String gridId, String selector) {
		return getEval("window.getComputedStyle(zk.Widget.$('$" + gridId
				+ "').$n().querySelector('" + selector + "')).display");
	}
}
