/* F110_ZK_6112_BiglistboxFrozenTickTest.java

	Purpose:
		Marble: the Biglistbox frozen-column tick is a draggable handle.

	Description:
		Marble's biglistbox.css did not style the tick, so it was 0px high and could not
		be grabbed with a pointer. It must stay clear of the cells and the sort header,
		or it swallows their clicks and the wheel scrolling over the divider.

	History:
		Mon Oct 26 2026, Created by hawk

Copyright (C) 2026 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.JavascriptExecutor;

import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

public class F110_ZK_6112_BiglistboxFrozenTickTest extends WebDriverTestCase {
	private static final String TICK = ".z-biglistbox-verticalbar-tick";
	private static final String FROZEN_HEADERS = ".z-biglistbox-head-frozen .z-biglistbox-header";
	private static final String CELLS = ".z-biglistbox-body-outer td, .z-biglistbox-header";
	private static final String HORIZONTAL_SCROLLBAR = ".z-biglistbox-wscroll-horizontal";
	private static final int COLUMN_WIDTH = 130;

	@Test
	public void tickIsAGrabbableHandle() {
		connect();
		waitResponse();

		Assertions.assertTrue(inBig(TICK).width() > 0, "the tick must have a width");
		Assertions.assertTrue(inBig(TICK).height() > 0, "the tick must have a height");
	}

	@Test
	public void tickDoesNotCoverCellsOrHeaders() {
		connect();
		waitResponse();

		Object overlapping = ((JavascriptExecutor) driver).executeScript(
				"var tick = jq('$big').find(arguments[0])[0].getBoundingClientRect();"
				+ "var hbar = jq('$big').find(arguments[2])[0].getBoundingClientRect();"
				+ "return Array.prototype.filter.call(jq('$big').find(arguments[1]), function (cell) {"
				// the body clips its cells at the scrollbar band, so only the part above it is visible
				+ "  var r = cell.getBoundingClientRect(), bottom = Math.min(r.bottom, hbar.top);"
				+ "  return r.width > 0 && bottom > r.top && r.left < tick.right && r.right > tick.left"
				+ "    && r.top < tick.bottom && bottom > tick.top;"
				+ "}).length;",
				TICK, CELLS, HORIZONTAL_SCROLLBAR);
		Assertions.assertEquals(0L, overlapping,
				"the tick must not overlap body cells or headers, or it takes their clicks");
	}

	@Test
	public void tickLiesInTheHorizontalScrollbarBand() {
		connect();
		waitResponse();

		Object inBand = ((JavascriptExecutor) driver).executeScript(
				"var tick = jq('$big').find(arguments[0])[0].getBoundingClientRect();"
				+ "var hbar = jq('$big').find(arguments[1])[0].getBoundingClientRect();"
				+ "var big = jq('$big')[0].getBoundingClientRect();"
				+ "return tick.top >= hbar.top && tick.bottom <= hbar.bottom"
				+ "  && tick.top >= big.top && tick.bottom <= big.bottom"
				+ "  && tick.left >= big.left && tick.right <= big.right;",
				TICK, HORIZONTAL_SCROLLBAR);
		Assertions.assertEquals(Boolean.TRUE, inBand,
				"the tick must sit inside the horizontal scrollbar band, clear of the cells");
	}

	@Test
	public void draggingTheTickFreezesAnotherColumn() {
		connect();
		waitResponse();
		Assertions.assertEquals(2, inBig(FROZEN_HEADERS).length());

		getActions().clickAndHold(toElement(inBig(TICK)))
				.moveByOffset(COLUMN_WIDTH, 0)
				.release()
				.perform();
		waitResponse();
		Assertions.assertEquals(3, inBig(FROZEN_HEADERS).length(),
				"dragging the tick one column to the right must freeze a third column");
	}

	/** ZK's {@code $id} selector matches nothing once two descendant steps follow it, hence find(). */
	private JQuery inBig(String selector) {
		return jq("$big").find(selector);
	}
}
