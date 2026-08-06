/* F110_ZK_6065MobileTest.java

	Purpose:

	Description:

	History:
		Mon Aug 17 17:30:00 CST 2026, Created by peakerlee

Copyright (C) 2026 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Collections;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.chrome.ChromeOptions;

import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

public class F110_ZK_6065MobileTest extends WebDriverTestCase {
	private static final int SCROLL_LEFT = 120;

	@Override
	protected ChromeOptions getWebDriverOptions() {
		return super.getWebDriverOptions()
				.setExperimentalOption("mobileEmulation", Collections.singletonMap("deviceName", "iPad"));
	}

	@Test
	public void testStickyFrozenCells() {
		connect("/test2/F110-ZK-6065.zul");
		waitResponse();

		assertSticky("$grid .z-column.z-frozen-sticky", "Grid header");
		assertSticky("$grid .z-row:first .z-row-inner.z-frozen-sticky", "Grid body");
		assertSticky("$gridGroup .z-cell.z-frozen-sticky", "Grid cell");
		assertSticky("$gridGroup .z-group-inner.z-frozen-sticky", "Grid group");
		assertSticky("$gridGroup .z-detail-outer.z-frozen-sticky", "Grid detail");
		assertSticky("$gridGroup .z-groupfoot-inner.z-frozen-sticky", "Grid groupfoot");
		assertSticky("$listbox .z-listheader.z-frozen-sticky", "Listbox header");
		assertSticky("$listbox .z-listitem:first .z-listcell.z-frozen-sticky", "Listbox body");
		assertSticky("$listboxGroup .z-listgroup-inner.z-frozen-sticky", "Listbox group");
		assertSticky("$listboxGroup .z-listgroupfoot-inner.z-frozen-sticky", "Listbox groupfoot");
		assertSticky("$tree .z-treecol.z-frozen-sticky", "Tree header");
		assertSticky("$tree .z-treerow .z-treecell.z-frozen-sticky", "Tree body");
		assertSticky("$gridAux .z-auxheader.z-frozen-sticky", "Grid auxhead");
	}

	/**
	 * A frozen cell may carry {@code position: sticky} yet still scroll away when a
	 * competing rule wins the cascade, so assert the rendered position too.
	 */
	@Test
	public void testFrozenCellsStayInPlaceAfterScroll() {
		connect("/test2/F110-ZK-6065.zul");
		waitResponse();

		assertStaysInPlace("$grid", "z-grid-body", "Grid",
				".z-column.z-frozen-sticky",
				".z-row:first .z-row-inner.z-frozen-sticky");
		assertStaysInPlace("$gridGroup", "z-grid-body", "Grid with group and detail",
				".z-cell.z-frozen-sticky",
				".z-group-inner.z-frozen-sticky",
				".z-detail-outer.z-frozen-sticky",
				".z-groupfoot-inner.z-frozen-sticky");
		assertStaysInPlace("$listbox", "z-listbox-body", "Listbox",
				".z-listheader.z-frozen-sticky",
				".z-listitem:first .z-listcell.z-frozen-sticky");
		assertStaysInPlace("$listboxGroup", "z-listbox-body", "Listbox with group",
				".z-listgroup-inner.z-frozen-sticky",
				".z-listgroupfoot-inner.z-frozen-sticky");
		assertStaysInPlace("$tree", "z-tree-body", "Tree",
				".z-treecol.z-frozen-sticky",
				".z-treerow .z-treecell.z-frozen-sticky");
		assertStaysInPlace("$gridAux", "z-grid-body", "Grid with auxhead",
				".z-auxheader.z-frozen-sticky",
				".z-column.z-frozen-sticky");
	}

	private void assertSticky(String selector, String name) {
		JQuery element = jq(selector).first();
		assertTrue(element.exists(), name + " should have a frozen sticky cell");
		assertEquals("sticky", element.css("position"), name + " should use sticky positioning");
		assertEquals("1", element.css("z-index"), name + " should appear above scrolling cells");
	}

	private void assertStaysInPlace(String meshSelector, String bodyClass, String name, String... cellSelectors) {
		double[] before = new double[cellSelectors.length];
		for (int i = 0; i < cellSelectors.length; i++)
			before[i] = left(meshSelector, cellSelectors[i]);

		jq(meshSelector + " .z-frozen-inner").scrollLeft(SCROLL_LEFT);
		waitResponse();

		// without a real scroll the checks below would pass vacuously
		assertEquals(SCROLL_LEFT, scrollLeft(meshSelector, bodyClass),
				name + " body should have scrolled");
		for (int i = 0; i < cellSelectors.length; i++)
			assertEquals(before[i], left(meshSelector, cellSelectors[i]), 1.0,
					name + " " + cellSelectors[i] + " should stay in place while the mesh scrolls");
	}

	private double left(String meshSelector, String cellSelector) {
		return Double.parseDouble(getEval("jq('" + meshSelector + "').find('" + cellSelector
				+ "')[0].getBoundingClientRect().left"));
	}

	private int scrollLeft(String meshSelector, String bodyClass) {
		return Integer.parseInt(getEval(
				"jq('" + meshSelector + "').find('." + bodyClass + "')[0].scrollLeft"));
	}
}
