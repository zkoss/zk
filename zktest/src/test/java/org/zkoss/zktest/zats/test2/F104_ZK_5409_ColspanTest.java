/* F104_ZK_5409_ColspanTest.java

	Purpose:

	Description:

	History:
		Mon May 18 17:53:22 CST 2026, Created by peakerlee

Copyright (C) 2026 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.JavascriptExecutor;
import org.zkoss.test.webdriver.WebDriverTestCase;

/**
 * Verifies that {@code _markStackingCells} correctly advances by
 * {@code colSpan} when stamping {@code data-label} on row cells.
 *
 * <p>Earlier the row loop advanced by {@code idx++} regardless of
 * {@code colSpan}, mis-aligning labels whenever a {@code <cell colspan="N">}
 * appeared in a row. The fix advances by {@code idx += td.colSpan} and
 * skips data-label / hide-class stamping for spanning cells (they are
 * not tied to a single column).
 */
public class F104_ZK_5409_ColspanTest extends WebDriverTestCase {

	@Test
	public void testRowCellColspanAlignsData() {
		connect("/test2/F104-ZK-5409-responsive-grid-colspan.zul");
		waitResponse();
		Dimension original = driver.manage().window().getSize();
		try {
			driver.manage().window().setSize(new Dimension(400, original.height));
			waitResponse();
			try { Thread.sleep(150); } catch (InterruptedException ignore) { /* */ }

			JavascriptExecutor js = (JavascriptExecutor) driver;
			// Collect data-label of each <td> in the single row, in DOM order.
			Object labelsJs = js.executeScript(
					"var g = zk.Widget.$('$g1').$n();"
					+ "var tds = g.querySelectorAll("
					+ "  ':scope > .z-grid-body tbody > tr.z-row > td');"
					+ "return Array.from(tds).map(function(t){"
					+ "  return t.hasAttribute('data-label')"
					+ "    ? t.getAttribute('data-label') : null;"
					+ "});");
			assertEquals("[A, null, D, E]", labelsJs.toString(),
					"Row with <cell colspan='2'>: td[0]→A, td[1] is the spanning "
					+ "cell (no label), td[2]→D, td[3]→E");

			// Sanity: column count is 5 (3 td elements x 1+2+1+1 = 5 columns)
			Object tdCount = js.executeScript(
					"var g = zk.Widget.$('$g1').$n();"
					+ "return g.querySelectorAll("
					+ "  ':scope > .z-grid-body tbody > tr.z-row > td').length;");
			assertEquals(4L, ((Number) tdCount).longValue(),
					"Row has 4 <td> elements (3 labels + 1 spanning cell)");

			// Sanity: spanning td has colspan=2
			Object spanAttr = js.executeScript(
					"var g = zk.Widget.$('$g1').$n();"
					+ "var tds = g.querySelectorAll("
					+ "  ':scope > .z-grid-body tbody > tr.z-row > td');"
					+ "return tds[1].colSpan;");
			assertEquals(2L, ((Number) spanAttr).longValue(),
					"td[1] is the spanning cell with colspan=2");

			// The spanning cell must NOT carry data-label (would be wrong column).
			Object spanLabel = js.executeScript(
					"var g = zk.Widget.$('$g1').$n();"
					+ "var tds = g.querySelectorAll("
					+ "  ':scope > .z-grid-body tbody > tr.z-row > td');"
					+ "return tds[1].getAttribute('data-label');");
			assertNull(spanLabel,
					"Spanning cell must NOT be stamped with a column label "
					+ "(it doesn't belong to any single column)");
		} finally {
			driver.manage().window().setSize(original);
		}
	}
}
