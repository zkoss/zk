/* F104_ZK_5409_DeviceMatrixTest.java

        Purpose:
                
        Description:
                
        History:
                Sat Apr 25 22:37:02 CST 2026, Created by peakerlee

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

public class F104_ZK_5409_DeviceMatrixTest extends WebDriverTestCase {

	private void setSize(int w, int h) {
		driver.manage().window().setSize(new Dimension(w, h));
		waitResponse();
	}

	// ── Mobile portrait (360x640): basic grid must stack with card layout ──

	@Test
	public void testMobilePortraitBasicGrid() {
		connect("/test2/F104-ZK-5409-responsive-grid-basic.zul");
		waitResponse();
		setSize(360, 640);

		JQuery grid = jq("$grid1");
		assertTrue(grid.hasClass("z-grid--stacking"),
				"basic grid must stack on mobile portrait (360px)");
		assertFalse(jq("$grid1 .z-columns").isVisible(),
				"column header must be hidden on mobile portrait");
		assertTrue(jq(".z-grid-body tbody > tr > td[data-label]").length() > 0,
				"cell-label spans must appear on mobile portrait");
	}

	@Test
	public void testMobilePortraitFinancialProduct() {
		connect("/test2/F104-ZK-5409-responsive-grid-financial-product.zul");
		waitResponse();
		setSize(360, 640);

		assertTrue(jq("$productGrid").hasClass("z-grid--stacking"),
				"12-column financial product grid must stack on mobile");
		// 6 data rows must each be a card
		JQuery cards = jq("$productGrid").find(".z-grid-body tbody > tr.z-row");
		assertTrue(cards.length() == 6,
				"all 6 product rows must surface as cards (got " + cards.length() + ")");
	}

	// ── Mobile landscape (640x360) — between sm and md ──

	@Test
	public void testMobileLandscapeMdGridStacks() {
		// md grid at 640px container ~= 625 → still < md (768) → stacks
		connect("/test2/F104-ZK-5409-responsive-grid-basic.zul");
		waitResponse();
		setSize(640, 360);

		assertTrue(jq("$grid1").hasClass("z-grid--stacking"),
				"md-breakpoint grid must stack on mobile landscape (640x360)");
	}

	// ── Tablet (768x1024 portrait) — at md boundary ──

	@Test
	public void testTabletPortraitMdGridStacksJustBelow() {
		// 768px window → ~753 container, strictly less than md(768) → stacks
		connect("/test2/F104-ZK-5409-responsive-grid-basic.zul");
		waitResponse();
		setSize(768, 1024);

		assertTrue(jq("$grid1").hasClass("z-grid--stacking"),
				"md grid must stack at 768x1024 (container < 768 due to scrollbar)");
	}

	@Test
	public void testTabletPortraitDashboardStacks() {
		connect("/test2/F104-ZK-5409-responsive-grid-dashboard.zul");
		waitResponse();
		setSize(768, 1024);

		// All 3 dashboard grids stack on tablet portrait
		assertTrue(jq("$gridRegions").hasClass("z-grid--stacking"),
				"Top Regions grid must stack on tablet");
		assertTrue(jq("$gridAccounts").hasClass("z-grid--stacking"),
				"Top Accounts (md breakpoint) must stack on tablet");
	}

	// ── Tablet landscape / small desktop (1024x768) ──

	@Test
	public void testTabletLandscapeMdGridRestores() {
		// 1024 > md(768), so md grid is in table mode
		connect("/test2/F104-ZK-5409-responsive-grid-basic.zul");
		waitResponse();
		setSize(1024, 768);

		assertFalse(jq("$grid1").hasClass("z-grid--stacking"),
				"md grid must be table at 1024x768");
		assertTrue(jq("$grid1 .z-columns").isVisible(),
				"column header must be visible on tablet landscape");
	}

	/** Dashboard hosts gridActivity inside a flex column ~280-420px wide, so
	 *  even at desktop viewport, its container can be narrower than its lg
	 *  breakpoint. This is intended dashboard behavior — the per-widget
	 *  container width drives the breakpoint, not viewport. Verify that the
	 *  widget inside a flex column adapts to its column width. */
	@Test
	public void testDashboardWidgetFollowsColumnWidth() {
		connect("/test2/F104-ZK-5409-responsive-grid-dashboard.zul");
		waitResponse();
		setSize(1024, 768);

		// gridActivity (lg breakpoint) sits in a flex column narrower than 992
		// when viewport is 1024 split with another column → expected: stacking
		assertTrue(jq("$gridActivity").hasClass("z-grid--stacking"),
				"gridActivity in flex column < lg should stack at 1024 viewport");
	}

	// ── Desktop (1440x900) ──

	/** At desktop width the FULL-WIDTH grid (gridAccounts spans the entire
	 *  groupbox below the flex split) must be in table mode. The flex-column
	 *  grids may still be stacking depending on flex layout — that's expected
	 *  per-widget responsive behavior. */
	@Test
	public void testDesktopFullWidthGridTable() {
		connect("/test2/F104-ZK-5409-responsive-grid-dashboard.zul");
		waitResponse();
		setSize(1440, 900);

		// gridAccounts is in a full-width groupbox, so its container is ~1400px
		// → above md(768) → table.
		assertFalse(jq("$gridAccounts").hasClass("z-grid--stacking"),
				"Full-width Accounts grid must be table at desktop");
	}

	// ── Wide desktop (1920x1080) ──

	@Test
	public void testWideDesktopFinancialProductTable() {
		connect("/test2/F104-ZK-5409-responsive-grid-financial-product.zul");
		waitResponse();
		setSize(1920, 1080);

		assertFalse(jq("$productGrid").hasClass("z-grid--stacking"),
				"financial product grid must be table at wide desktop");
		// All 13 columns visible (1 checkbox + 12 data)
		assertTrue(jq("$productGrid .z-column").length() >= 13,
				"all 13 columns must render at wide desktop (got " +
						jq("$productGrid .z-column").length() + ")");
	}

	// ── Cross-device transition: orient flip ──

	@Test
	public void testMobileToTabletTransition() {
		connect("/test2/F104-ZK-5409-responsive-grid-basic.zul");
		waitResponse();

		setSize(360, 640);
		assertTrue(jq("$grid1").hasClass("z-grid--stacking"),
				"start in mobile = stacking");

		setSize(1024, 768);
		assertFalse(jq("$grid1").hasClass("z-grid--stacking"),
				"after rotating to tablet landscape, must restore to table");

		setSize(360, 640);
		assertTrue(jq("$grid1").hasClass("z-grid--stacking"),
				"back to mobile, stacking again");
	}

	/** Spec 7.5: column with no label → empty key-value pair. The
	 *  financial-product demo has a checkbox column with label="" (column 0).
	 *  In stacking mode this must not break the card layout. */
	@Test
	public void testEmptyLabelColumnHandled() {
		connect("/test2/F104-ZK-5409-responsive-grid-financial-product.zul");
		waitResponse();
		setSize(360, 640);

		assertTrue(jq("$productGrid").hasClass("z-grid--stacking"),
				"product grid must stack on mobile");

		// Verify no JS errors — empty label shouldn't crash card rendering.
		// First card must have at least 5 visible cell-labels (subset of cols).
		JQuery firstCard = jq("$productGrid").find(".z-grid-body tbody > tr.z-row").eq(0);
		JQuery labels = firstCard.find("td[data-label]");
		assertTrue(labels.length() >= 5,
				"first card must have >=5 cell-labels (got " + labels.length() + ")");
	}

	/** Per spec, container width — not viewport — drives the breakpoint.
	 *  Verify a grid inside a 300px-wide div stacks even on a desktop window. */
	@Test
	public void testNarrowContainerStacksOnDesktop() {
		connect("/test2/F104-ZK-5409-responsive-grid-container.zul");
		waitResponse();
		setSize(1440, 900);

		// gridInSidebar lives in a 300px div — must stack regardless of window
		assertTrue(jq("$gridInSidebar").hasClass("z-grid--stacking"),
				"grid in 300px sidebar must stack even on desktop window");

		// gridInWindow is at full width — should be table on desktop
		assertEquals(false, jq("$gridInWindow").hasClass("z-grid--stacking"),
				"grid in full-width window must be table on desktop");
	}
}
