package org.zkoss.zktest.zats.test2;

import static org.junit.Assert.assertTrue;
import org.junit.Test;

import org.zkoss.zktest.zats.WebDriverTestCase;
import org.zkoss.zktest.zats.ztl.JQuery;

public class B96_ZK_5029Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		assertContentTextNoWrap("listbox1", "listbox-header", "listitem");
		assertContentTextNoWrap("listbox2", "listbox-header", "listitem");
		assertContentTextNoWrap("grid1", "grid-header", "row");
		assertContentTextNoWrap("tree1", "tree-header", "tree-row");
	}

	void assertContentTextNoWrap(String id, String header, String row) {
		final String titleWidgetSelector = "$" + id + " .z-" + header;
		final String contentTextWidgetSelector = "$" + id + " .z-" + row;
		// The `5` added to `height()` is to account for floating-point imprecision and padding inconsistencies.
		// In this testcase, line heights are much larger than 5px.
		final int titleHeight = 5 + jq(titleWidgetSelector).height();
		for (JQuery contentTextWidget : jq(contentTextWidgetSelector)) {
			final int contentTextHeight = contentTextWidget.height();
			// In this testcase, text in header are so short that they wouldn't wrap, so to make sure that long text in
			// other rows didn't wrap, we compare their row heights to that of headers.
			assertTrue(contentTextHeight <= titleHeight);
		}
	}
}