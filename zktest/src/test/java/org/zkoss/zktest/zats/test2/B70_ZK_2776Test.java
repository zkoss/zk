package org.zkoss.zktest.zats.test2;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.Element;
import org.zkoss.test.webdriver.ztl.JQuery;
import org.zkoss.test.webdriver.ztl.Widget;

public class B70_ZK_2776Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		JQuery grid = jq("@grid");
		JQuery firstColumn = grid.find(".z-column").first();
		mouseOver(firstColumn);
		waitResponse();
		Element column_button = firstColumn.toWidget().$n("btn");
		click(column_button);
		waitResponse();
		click(jq(".z-menupopup-open .z-menupopup-content .z-menuitem-checkable").get(3));
		waitResponse();
		Widget wgt = jq(grid).toWidget();
		eval(wgt + ".frozen._doScrollNow(" + 2860 + ")");
		waitResponse();
		eval(wgt + ".frozen._doScrollNow(" + -2860 + ")");
		waitResponse();
		assertEquals(jq("@grid .z-column:first").outerWidth(), jq("@grid .z-rows .z-row:first .z-cell:first").outerWidth(), 1);
		assertEquals(jq("@grid .z-column:eq(1)").outerWidth(), jq("@grid .z-rows .z-row:first .z-cell:eq(1)").outerWidth(), 1);
		assertEquals(jq("@grid .z-column:eq(2)").outerWidth(), jq("@grid .z-rows .z-row:first .z-cell:eq(2)").outerWidth(), 1);
		assertEquals(jq("@grid .z-column:eq(4)").outerWidth(), jq("@grid .z-rows .z-row:first .z-cell:eq(4)").outerWidth(), 1);

		//show column 4 again
		mouseOver(firstColumn);
		waitResponse();
		click(column_button);
		waitResponse();
		click(jq(".z-menupopup-open .z-menupopup-content .z-menuitem-checkable").get(3));
		waitResponse();
		eval(wgt + ".frozen._doScrollNow(" + 2860 + ")");
		waitResponse();
		eval(wgt + ".frozen._doScrollNow(" + -2860 + ")");
		waitResponse();
		eval(wgt + ".frozen._doScrollNow(" + 2860 + ")");
		waitResponse();
		eval(wgt + ".frozen._doScrollNow(" + -2860 + ")");
		waitResponse();
		assertTrue(jq("@auxheader").isVisible());
	}
}
