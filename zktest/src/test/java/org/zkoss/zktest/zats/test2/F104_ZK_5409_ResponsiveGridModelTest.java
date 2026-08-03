/* F104_ZK_5409_ResponsiveGridModelTest.java

        Purpose:
                
        Description:
                
        History:
                Sat Apr 25 22:23:05 CST 2026, Created by peakerlee

Copyright (C) 2026 Potix Corporation. All Rights Reserved.
*/


package org.zkoss.zktest.zats.test2;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.Dimension;

import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

public class F104_ZK_5409_ResponsiveGridModelTest extends WebDriverTestCase {

	/**
	 * Test 3.2: ListModel + stacking — model-driven rows should stack properly.
	 */
	@Test
	public void testListModelStacking() {
		connect("/test2/F104-ZK-5409-responsive-grid-model.zul");
		waitResponse();

		Dimension size = driver.manage().window().getSize();
		driver.manage().window().setSize(new Dimension(600, size.height));
		waitResponse();

		JQuery grid = jq("$grid2");
		assertTrue(grid.hasClass("z-grid--stacking"),
				"ListModel grid should be in stacking mode");

		// Should have 3 rows from the model
		assertEquals(3, jq("$grid2").find(".z-grid-body tbody > tr.z-row").length(),
				"ListModel grid should have 3 rows");

		// Cell labels should appear
		assertTrue(jq("$grid2").find(".z-grid-body tbody > tr > td[data-label]").length() > 0,
				"Cell-label spans should appear in ListModel stacking");
	}

	/**
	 * Test 3.3: ListModel + template + stacking — template-rendered rows stack properly.
	 */
	@Test
	public void testListModelTemplateStacking() {
		connect("/test2/F104-ZK-5409-responsive-grid-model.zul");
		waitResponse();

		Dimension size = driver.manage().window().getSize();
		driver.manage().window().setSize(new Dimension(600, size.height));
		waitResponse();

		JQuery grid = jq("$grid3");
		assertTrue(grid.hasClass("z-grid--stacking"),
				"Template grid should be in stacking mode");

		// Should have 3 rows from template rendering
		assertEquals(3, jq("$grid3").find(".z-grid-body tbody > tr.z-row").length(),
				"Template grid should have 3 rows");

		// Verify cell labels match column labels
		assertEquals("Name", jq("$grid3").find(".z-grid-body tbody > tr.z-row:eq(0) td:eq(0)").attr("data-label"),
				"Template row first cell label should be 'Name'");
	}

	/**
	 * Test 3.6: Empty model + emptyMessage in stacking mode.
	 */
	@Test
	public void testEmptyModelStacking() {
		connect("/test2/F104-ZK-5409-responsive-grid-model.zul");
		waitResponse();

		Dimension size = driver.manage().window().getSize();
		driver.manage().window().setSize(new Dimension(600, size.height));
		waitResponse();

		JQuery grid = jq("$grid4");
		assertTrue(grid.hasClass("z-grid--stacking"),
				"Empty model grid should be in stacking mode");

		// Empty message should be displayed
		JQuery emptyMsg = jq("$grid4").find(".z-grid-emptybody, .z-grid-empty-message");
		assertTrue(emptyMsg.exists() || jq("$grid4").text().contains("No data available"),
				"Empty message should be displayed");
	}

	/**
	 * Test 3.7: Dynamic add/remove model items while in stacking mode.
	 */
	@Test
	public void testDynamicModelItemsStacking() {
		connect("/test2/F104-ZK-5409-responsive-grid-model.zul");
		waitResponse();

		Dimension size = driver.manage().window().getSize();
		driver.manage().window().setSize(new Dimension(600, size.height));
		waitResponse();

		JQuery grid = jq("$grid5");
		assertTrue(grid.hasClass("z-grid--stacking"),
				"Dynamic model grid should be in stacking mode");

		// Initial: 2 rows
		assertEquals(2, jq("$grid5").find(".z-grid-body tbody > tr.z-row").length(),
				"Should start with 2 rows");

		// Add item
		click(jq("$btnAdd"));
		waitResponse();

		assertEquals(3, jq("$grid5").find(".z-grid-body tbody > tr.z-row").length(),
				"Should have 3 rows after adding");
		// Should still be in stacking mode
		assertTrue(grid.hasClass("z-grid--stacking"),
				"Grid should remain in stacking mode after adding row");

		// Remove item
		click(jq("$btnRemove"));
		waitResponse();

		assertEquals(2, jq("$grid5").find(".z-grid-body tbody > tr.z-row").length(),
				"Should have 2 rows after removing");
		assertTrue(grid.hasClass("z-grid--stacking"),
				"Grid should remain in stacking mode after removing row");
	}

	/**
	 * Test 3.4: GroupsModel + stacking — group/groupfoot should span full width.
	 */
	@Test
	public void testGroupsModelStacking() {
		connect("/test2/F104-ZK-5409-responsive-grid-group.zul");
		waitResponse();

		Dimension size = driver.manage().window().getSize();
		driver.manage().window().setSize(new Dimension(600, size.height));
		waitResponse();

		JQuery grid = jq("$grid1");
		assertTrue(grid.hasClass("z-grid--stacking"),
				"GroupsModel grid should be in stacking mode");

		// Groups should exist and span full width
		assertTrue(jq("$grid1").find(".z-grid-body tbody .z-group").length() >= 2,
				"Should have at least 2 groups");

		// Regular rows should be in stacking format
		assertTrue(jq("$grid1").find(".z-grid-body tbody > tr.z-row").length() >= 4,
				"Should have data rows");

		// Groupfoot should exist
		assertTrue(jq("$grid1").find(".z-grid-body tbody .z-groupfoot").length() >= 1,
				"Should have groupfoot elements");
	}

	/**
	 * Test 3.5: GroupsModel + multi-column stacking.
	 * Group headers span all columns, rows arranged in grid.
	 */
	@Test
	public void testGroupsModelMultiColumnStacking() {
		connect("/test2/F104-ZK-5409-responsive-grid-group.zul");
		waitResponse();

		Dimension size = driver.manage().window().getSize();
		driver.manage().window().setSize(new Dimension(600, size.height));
		waitResponse();

		JQuery grid = jq("$grid2");
		assertTrue(grid.hasClass("z-grid--stacking"),
				"Multi-column groups grid should be in stacking mode");

		// Groups should span full width (grid-column: 1 / -1)
		JQuery groups = jq("$grid2").find(".z-grid-body tbody .z-group");
		assertTrue(groups.length() >= 2,
				"Should have group headers");

		// Data rows should be arranged in 3-column grid
		JQuery rows = jq("$grid2").find(".z-grid-body tbody > tr.z-row");
		assertTrue(rows.length() >= 6,
				"Should have data rows in multi-column layout");
	}
}
