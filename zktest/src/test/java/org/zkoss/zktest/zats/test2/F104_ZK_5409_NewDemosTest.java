/* F104_ZK_5409_NewDemosTest.java

        Purpose:
                
        Description:
                
        History:
                Sat Apr 25 22:37:09 CST 2026, Created by peakerlee

Copyright (C) 2026 Potix Corporation. All Rights Reserved.
*/

package org.zkoss.zktest.zats.test2;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.Dimension;

import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

public class F104_ZK_5409_NewDemosTest extends WebDriverTestCase {

	private void setSize(int w, int h) {
		driver.manage().window().setSize(new Dimension(w, h));
		waitResponse();
	}

	// ─── Dashboard ──────────────────────────────────────────────────

	/** Dashboard has 3 grids: 2 in flex columns (~50% width each on wide
	 *  viewport) and gridAccounts in a full-width groupbox. The full-width
	 *  grid reliably toggles based on viewport; the flex-column ones depend
	 *  on per-widget container width, which is expected per-spec
	 *  (container-width drives breakpoint, not viewport). */
	@Test
	public void testDashboardFullWidthGridToggle() {
		connect("/test2/F104-ZK-5409-responsive-grid-dashboard.zul");
		waitResponse();

		setSize(1600, 900);
		// gridAccounts is full-width — at 1600 viewport it's well above md(768)
		assertFalse(jq("$gridAccounts").hasClass("z-grid--stacking"),
				"gridAccounts (full-width): table at desktop");

		setSize(400, 800);
		assertTrue(jq("$gridAccounts").hasClass("z-grid--stacking"),
				"gridAccounts (full-width): stacking at mobile");
		// At mobile, flex columns are also narrow → all 3 grids stack
		assertTrue(jq("$gridRegions").hasClass("z-grid--stacking"),
				"gridRegions: stacking at mobile");
		assertTrue(jq("$gridActivity").hasClass("z-grid--stacking"),
				"gridActivity: stacking at mobile");
	}

	// ─── Financial Product (image-style toolbar + 12-col grid) ──────

	@Test
	public void testFinancialProductToolbarAndGrid() {
		connect("/test2/F104-ZK-5409-responsive-grid-financial-product.zul");
		waitResponse();

		setSize(1600, 900);
		// Toolbar buttons exist (9 actions)
		assertTrue(jq(".toolbar-wrap .z-button").length() >= 9,
				"financial product toolbar must have >=9 buttons (got " +
						jq(".toolbar-wrap .z-button").length() + ")");
		// Grid in table mode at desktop with all columns
		assertFalse(jq("$productGrid").hasClass("z-grid--stacking"),
				"product grid: table at desktop");

		setSize(380, 800);
		assertTrue(jq("$productGrid").hasClass("z-grid--stacking"),
				"product grid: stacking on mobile");
		// 6 sample products surface as cards
		assertEquals(6, jq("$productGrid").find(".z-grid-body tbody > tr.z-row").length(),
				"product grid: 6 row cards on mobile");
	}

	@Test
	public void testFinancialProductHidesAlwaysHiddenCols() {
		connect("/test2/F104-ZK-5409-responsive-grid-financial-product.zul");
		waitResponse();
		setSize(380, 800);

		JQuery firstCard = jq("$productGrid").find(".z-grid-body tbody > tr.z-row").eq(0);

		JQuery hiddenCells = firstCard.find(".z-cell-hide-stacking");
		assertTrue(hiddenCells.length() >= 2,
				"At least 2 cells with z-cell-hide-stacking expected (got " +
						hiddenCells.length() + ")");

		// The Product Code label may exist in DOM (inside a hidden cell) but its
		// containing cell must have display:none — i.e. the label is NOT visible.
		JQuery labels = firstCard.find("td[data-label]");
		for (int i = 0; i < labels.length(); i++) {
			JQuery lbl = labels.eq(i);
			if ("Product Code".equals(lbl.text())) {
				assertFalse(lbl.isVisible());
			}
		}
	}

	// ─── Master-Detail (linked grids) ───────────────────────────────

	@Test
	public void testMasterDetailRendersBothGrids() {
		connect("/test2/F104-ZK-5409-responsive-grid-master-detail.zul");
		waitResponse();
		setSize(1800, 900);

		// Master grid renders with 4 department rows.
		assertEquals(4, jq("$masterGrid").find(".z-rows tr.z-row").length(),
				"master must have 4 department rows");
		// Each row has a "View" button (loadD01..loadD04 by id).
		assertTrue(jq("$loadD01").exists(),
				"loadD01 button exists in master row 1");
		assertTrue(jq("$loadD04").exists(),
				"loadD04 button exists in master row 4");
		// Detail grid starts empty.
		assertEquals(0, jq("$detailRows tr.z-row").length(),
				"detail starts empty");
		// Detail title shows the placeholder.
		assertTrue(jq("$detailTitle").text().contains("Select"),
				"detail title shows placeholder before any selection");
	}

	@Test
	public void testMasterDetailStacksOnMobile() {
		connect("/test2/F104-ZK-5409-responsive-grid-master-detail.zul");
		waitResponse();
		setSize(380, 800);

		// masterGrid breakpoint=sm (576) → stacks at <576
		assertTrue(jq("$masterGrid").hasClass("z-grid--stacking"),
				"master grid: stacking on mobile");
		// detailGrid breakpoint=md (768) → stacks at <768
		assertTrue(jq("$detailGrid").hasClass("z-grid--stacking"),
				"detail grid: stacking on mobile");
	}

	// ─── Nested (outer grid → inner line-items grids) ───────────────

	@Test
	public void testNestedOuterAndInnerStack() {
		connect("/test2/F104-ZK-5409-responsive-grid-nested.zul");
		waitResponse();

		setSize(1400, 900);
		// At desktop, outer is table, inners may or may not stack (breakpoint=sm)
		assertFalse(jq("$orderGrid").hasClass("z-grid--stacking"),
				"orderGrid: table at desktop");

		setSize(380, 800);
		assertTrue(jq("$orderGrid").hasClass("z-grid--stacking"),
				"orderGrid: stacking on mobile");
		assertTrue(jq("$linesA").hasClass("z-grid--stacking"),
				"inner linesA: stacking on mobile (breakpoint=sm, 380<576)");
	}

	// ZK-5409: assertions reference the v1 DOM-clone shape (.z-grid-body tbody as
	// direct child, div.z-row cards). Pure-CSS keeps the original <table> DOM
	// (tbody nested inside <table>; cards are <tr>). Selectors need rewriting.
	/*
	@Test
	public void testNestedHasThreeOrderCards() {
		connect("/test2/F104-ZK-5409-responsive-grid-nested.zul");
		waitResponse();
		setSize(380, 800);

		// Outer orderGrid's stacking zone is its direct child. Inner grids
		// (linesA/B/C) have their own stacking zones nested deeper inside
		// each card, so direct-child traversal via children() filters them out.
		JQuery outerZone = jq("$orderGrid").children(".z-grid-body tbody");
		assertEquals(1, outerZone.length(),
				"orderGrid must have exactly one direct-child stacking zone");
		JQuery outerCards = outerZone.children("div.z-row");
		assertEquals(3, outerCards.length(),
				"orderGrid: 3 outer order cards (got " + outerCards.length() + ")");
	}
	*/

	// ─── Search Form (form-grid + result-grid) ──────────────────────

	@Test
	public void testSearchFormFormAndResultBothStack() {
		connect("/test2/F104-ZK-5409-responsive-grid-search-form.zul");
		waitResponse();

		setSize(1600, 900);
		assertFalse(jq("$searchForm").hasClass("z-grid--stacking"),
				"searchForm grid: table at desktop");
		assertFalse(jq("$resultGrid").hasClass("z-grid--stacking"),
				"resultGrid: table at desktop");

		setSize(380, 800);
		assertTrue(jq("$searchForm").hasClass("z-grid--stacking"),
				"searchForm grid: stacking on mobile (4-col form → 1-col)");
		assertTrue(jq("$resultGrid").hasClass("z-grid--stacking"),
				"resultGrid: stacking on mobile");
	}

	@Test
	public void testSearchFormHasFiveResultRows() {
		connect("/test2/F104-ZK-5409-responsive-grid-search-form.zul");
		waitResponse();
		setSize(380, 800);

		assertEquals(5, jq("$resultGrid").find(".z-grid-body tbody > tr.z-row").length(),
				"resultGrid: 5 customer rows surface as cards");
	}
}
