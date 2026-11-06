/* B110_ZK_6112_MenubarVerticalHflexTest.java

	Purpose:
		Marble: a vertical Menubar with hflex="min" is as wide as its widest item.

	Description:
		Marble laid the vertical menubar out as a block-level flex box that stretches its items, so
		hflex="min", which measures the children at width:auto, measured the whole row.

	History:
		Wed Nov 04 2026, Created by hawk

Copyright (C) 2026 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;

public class B110_ZK_6112_MenubarVerticalHflexTest extends WebDriverTestCase {
	/** Sub-pixel layout can round a width by a pixel. */
	private static final int EPSILON = 1;
	private static final int FIXED_WIDTH = 300;

	@BeforeEach
	public void marbleOnly() {
		Assumptions.assumeFalse("iceblue".equals(System.getProperty("zktest.theme")),
				"asserts Marble's flex layout; IceBlue's inline-table items keep their own width and padding");
	}

	@Test
	public void hflexMinFitsTheWidestItem() {
		connect();
		waitResponse();

		int bar = jq("$min").outerWidth();
		int page = jq("body").width();
		int widest = jq("$widest").outerWidth();
		Assertions.assertTrue(bar < page / 2, "hflex=\"min\" must not take the whole row: menubar " + bar + "px, page " + page + "px");
		Assertions.assertTrue(Math.abs(bar - widest) <= EPSILON,
				"the menubar must be as wide as its widest item: menubar " + bar + "px, item " + widest + "px");
	}

	@Test
	public void aSetWidthStillStretchesTheItems() {
		connect();
		waitResponse();

		Assertions.assertTrue(Math.abs(jq("$fixed").outerWidth() - FIXED_WIDTH) <= EPSILON, "width=\"300px\" is kept");
		Assertions.assertTrue(Math.abs(jq("$fixedItem").outerWidth() - FIXED_WIDTH) <= EPSILON,
				"the items of a vertical menubar with a set width fill it");
	}
}
