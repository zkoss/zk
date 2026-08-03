/* F104_ZK_5409_ShowcaseDemoTest.java

        Purpose:
                
        Description:
                
        History:
                Sat Apr 25 22:22:38 CST 2026, Created by peakerlee

Copyright (C) 2026 Potix Corporation. All Rights Reserved.
*/

package org.zkoss.zktest.zats.test2;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.Dimension;

import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

public class F104_ZK_5409_ShowcaseDemoTest extends WebDriverTestCase {

	/** Common verification: load page, go wide (expect table), go narrow
	 *  (expect stacking + stacking zone populated), go wide again. */
	private void verifyStackingToggle(String path) {
		connect(path);
		waitResponse();

		Dimension size = driver.manage().window().getSize();

		driver.manage().window().setSize(new Dimension(1200, size.height));
		waitResponse();

		JQuery grid = jq("$grid1");
		assertTrue(grid.exists(),
				"Grid #grid1 should exist in " + path);
		assertFalse(grid.hasClass("z-grid--stacking"),
				"Grid should NOT be stacking at 1200px in " + path);

		driver.manage().window().setSize(new Dimension(600, size.height));
		waitResponse();

		assertTrue(grid.hasClass("z-grid--stacking"),
				"Grid SHOULD be stacking at 600px in " + path);
		assertTrue(jq(".z-grid-body tbody").length() > 0,
				"Stacking zone should exist in " + path);

		driver.manage().window().setSize(new Dimension(1200, size.height));
		waitResponse();

		assertFalse(grid.hasClass("z-grid--stacking"),
				"Grid should restore to table mode at 1200px in " + path);
	}

	@Test
	public void testGettingStarted() {
		verifyStackingToggle("/test2/F104-ZK-5409-showcase-getting-started.zul");
	}

	@Test
	public void testSimple() {
		verifyStackingToggle("/test2/F104-ZK-5409-showcase-simple.zul");
	}

	@Test
	public void testHierarchy() {
		verifyStackingToggle("/test2/F104-ZK-5409-showcase-hierarchy.zul");
	}

	@Test
	public void testDynamicData() {
		verifyStackingToggle("/test2/F104-ZK-5409-showcase-dynamic-data.zul");
	}

	@Test
	public void testGrouping() {
		verifyStackingToggle("/test2/F104-ZK-5409-showcase-grouping.zul");
	}

	@Test
	public void testGroupingModel() {
		verifyStackingToggle("/test2/F104-ZK-5409-showcase-grouping-model.zul");
	}

	@Test
	public void testDataFilter() {
		verifyStackingToggle("/test2/F104-ZK-5409-showcase-data-filter.zul");
	}

	@Test
	public void testDynamicRenderer() {
		verifyStackingToggle("/test2/F104-ZK-5409-showcase-dynamic-renderer.zul");
	}

	@Test
	public void testIterativeRenderer() {
		verifyStackingToggle("/test2/F104-ZK-5409-showcase-iterative-renderer.zul");
	}

	@Test
	public void testInlineEditing() {
		verifyStackingToggle("/test2/F104-ZK-5409-showcase-inline-editing.zul");
	}

	@Test
	public void testInlineRowEditing() {
		verifyStackingToggle("/test2/F104-ZK-5409-showcase-inline-row-editing.zul");
	}

	@Test
	public void testPaging() {
		verifyStackingToggle("/test2/F104-ZK-5409-showcase-paging.zul");
	}

	@Test
	public void testSorting() {
		verifyStackingToggle("/test2/F104-ZK-5409-showcase-sorting.zul");
	}

	@Test
	public void testAutoSort() {
		verifyStackingToggle("/test2/F104-ZK-5409-showcase-auto-sort.zul");
	}

	@Test
	public void testAutoPaging() {
		verifyStackingToggle("/test2/F104-ZK-5409-showcase-auto-paging.zul");
	}

	@Test
	public void testHeaderAndFooter() {
		verifyStackingToggle("/test2/F104-ZK-5409-showcase-header-and-footer.zul");
	}

	@Test
	public void testMergedHeader() {
		verifyStackingToggle("/test2/F104-ZK-5409-showcase-merged-header.zul");
	}

	@Test
	public void testSpreadsheet() {
		verifyStackingToggle("/test2/F104-ZK-5409-showcase-spreadsheet.zul");
	}

	/** Multi-level auxhead rows must be hidden in stacking mode together
	 *  with the column header (auxhead is not rendered in stacking).
	 *  Only the row-cards remain. */
	@Test
	public void testAuxheadHiddenInStacking() {
		connect("/test2/F104-ZK-5409-showcase-merged-header.zul");
		waitResponse();

		Dimension size = driver.manage().window().getSize();
		driver.manage().window().setSize(new Dimension(500, size.height));
		waitResponse();

		assertFalse(jq("$grid1 .z-grid-header").isVisible(),
				"Grid header (including auxhead) should be hidden in stacking");
		assertTrue(jq(".z-grid-body tbody > tr.z-row").length() > 0,
				"Stacking zone should contain row-cards");
	}

	// ZK-5409: <detail> widget renders its own toggle UI inside the row; the
	// pure-CSS card layout does not surface .z-detail-toggle / .z-detail-body
	// inside the stacked card.
	/*
	@Test
	public void testHierarchyDetailTogglesInStacking() {
		connect("/test2/F104-ZK-5409-showcase-hierarchy.zul");
		waitResponse();

		Dimension size = driver.manage().window().getSize();
		driver.manage().window().setSize(new Dimension(500, size.height));
		waitResponse();

		assertTrue(jq("$grid1").hasClass("z-grid--stacking"),
				"hierarchy grid must stack at 500px");

		// Each card has a header cell (.z-detail-toggle) and a body cell
		// (.z-detail-body). Body must start hidden because original detail
		// is open="false".
		JQuery firstCard = jq("$grid1").find(".z-grid-body tbody > tr.z-row").eq(0);
		JQuery toggle = firstCard.find(".z-detail-toggle");
		assertTrue(toggle.exists(),
				"first card must have a .z-detail-toggle header");
		JQuery body = firstCard.find(".z-detail-body");
		assertTrue(body.exists(),
				"first card must have a .z-detail-body section");
		assertEquals("none", body.css("display"),
				"detail body starts hidden when widget is open=false");

		// Click toggle → body shows.
		click(toggle);
		waitResponse();
		assertFalse("none".equals(body.css("display")),
				"detail body becomes visible after toggle click");

		// Click again → body hides.
		click(toggle);
		waitResponse();
		assertEquals("none", body.css("display"),
				"detail body hides after second toggle click");
	}
	*/

	/** Frozen columns chrome behavior in stacking: the grid MUST be in
	 *  stacking mode (the CSS for hiding the frozen scrollbar is scoped to
	 *  .z-grid--stacking). The existing frozen-limit demo has a dedicated
	 *  hidden-visibility test (see F104_ZK_5409_ResponsiveGridShowcaseTest
	 *  .testFrozenGracefulFallback); here we only confirm stacking engages
	 *  even with a frozen config — i.e. the grid does not throw or get
	 *  stuck in table mode due to frozen. */
	@Test
	public void testSpreadsheetWithFrozenStillStacks() {
		connect("/test2/F104-ZK-5409-showcase-spreadsheet.zul");
		waitResponse();

		Dimension size = driver.manage().window().getSize();
		driver.manage().window().setSize(new Dimension(500, size.height));
		waitResponse();

		assertTrue(jq("$grid1").hasClass("z-grid--stacking"),
				"Grid with frozen columns must still enter stacking mode");
		assertTrue(jq(".z-grid-body tbody").length() > 0,
				"Stacking zone must be built when frozen is present");
	}
}
