/* F104_ZK_5409_NestedIsolationTest.java

	Purpose:

	Description:

	History:
		Tue May 12 10:22:52 CST 2026, Created by peakerlee

Copyright (C) 2026 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.JavascriptExecutor;
import org.zkoss.test.webdriver.WebDriverTestCase;

/**
 * Nested responsive grids must not cross-stamp each other's column labels.
 * Verifies that {@code Grid._markStackingCells} correctly uses
 * {@code :scope &gt; .z-grid-body} (and footer equivalents) to limit
 * label injection to a single grid's own cells.
 */
public class F104_ZK_5409_NestedIsolationTest extends WebDriverTestCase {

	@Test
	public void testInnerCellsKeepOwnColumnLabels() {
		connect("/test2/F104-ZK-5409-nested.zul");
		waitResponse();
		Dimension original = driver.manage().window().getSize();
		try {
			driver.manage().window().setSize(new Dimension(400, original.height));
			waitResponse();
			// Allow ResizeObserver to fire on both grids
			try { Thread.sleep(150); } catch (InterruptedException ignore) { /* */ }

			JavascriptExecutor js = (JavascriptExecutor) driver;

			// Both grids should now be in stacking mode.
			Object outerStacking = js.executeScript(
					"return zk.Widget.$('$outer')._responsiveMode;");
			Object innerStacking = js.executeScript(
					"return zk.Widget.$('$inner')._responsiveMode;");
			assertEquals("stacking", outerStacking, "Outer grid should be stacking");
			assertEquals("stacking", innerStacking, "Inner grid should be stacking");

			// Read the data-label on the first data <td> of the inner grid.
			// `:scope > .z-grid-body > table > tbody > tr.z-row > td` constrains
			// to the inner grid's own body. Read the first two cell labels.
			Object innerLabel0 = js.executeScript(
					"var n = zk.Widget.$('$inner').$n();"
					+ "var tds = n.querySelectorAll("
					+ "  ':scope > .z-grid-body tbody > tr.z-row > td');"
					+ "return tds.length > 0 ? tds[0].getAttribute('data-label') : null;");
			Object innerLabel1 = js.executeScript(
					"var n = zk.Widget.$('$inner').$n();"
					+ "var tds = n.querySelectorAll("
					+ "  ':scope > .z-grid-body tbody > tr.z-row > td');"
					+ "return tds.length > 1 ? tds[1].getAttribute('data-label') : null;");

			assertEquals("Inner-X", innerLabel0,
					"Inner grid first cell should carry inner column label, not outer");
			assertEquals("Inner-Y", innerLabel1,
					"Inner grid second cell should carry inner column label, not outer");
			assertNotEquals("Outer-A", innerLabel0,
					"Inner cells must NOT be cross-stamped with outer column labels");
			assertNotEquals("Outer-B", innerLabel1,
					"Inner cells must NOT be cross-stamped with outer column labels");

			// Sanity-check the outer first cell carries an outer label (not an inner one).
			Object outerLabel0 = js.executeScript(
					"var n = zk.Widget.$('$outer').$n();"
					+ "var tds = n.querySelectorAll("
					+ "  ':scope > .z-grid-body tbody > tr.z-row > td');"
					+ "return tds.length > 0 ? tds[0].getAttribute('data-label') : null;");
			assertTrue("Outer-A".equals(outerLabel0) || "Outer-B".equals(outerLabel0),
					"Outer first cell should have an outer column label, not an inner one. "
					+ "Got: " + outerLabel0);
		} finally {
			driver.manage().window().setSize(original);
		}
	}
}
