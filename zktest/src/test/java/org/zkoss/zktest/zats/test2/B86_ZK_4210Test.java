/* B86_ZK_4210Test.java

	Purpose:
		
	Description:
		
	History:
		Mon May 06 11:30:52 CST 2019, Created by rudyhuang

Copyright (C) 2019 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.hamcrest.Matchers.greaterThan;

import org.hamcrest.MatcherAssert;
import org.junit.Test;

import org.zkoss.zktest.zats.WebDriverTestCase;
import org.zkoss.zktest.zats.ztl.JQuery;

/**
 * @author rudyhuang
 */
public class B86_ZK_4210Test extends WebDriverTestCase {
	private static final int DRAGGING_THRESHOLD = 5;

	@Test
	public void testGrid() {
		connect();

		jq("html").scrollLeft(200);

		JQuery body = jq("@grid .z-grid-body");
		JQuery column5 = jq("@column:eq(4)");
		body.scrollLeft(body.scrollWidth());
		waitResponse();

		resizeColumn(column5);
		MatcherAssert.assertThat(jq("#zk_hdghost").offsetLeft(), greaterThan(column5.offsetLeft()));
	}

	@Test
	public void testListbox() {
		connect();

		jq("html").scrollLeft(200);

		JQuery body = jq("@listbox .z-listbox-body");
		JQuery column5 = jq("@listheader:eq(4)");
		body.scrollLeft(body.scrollWidth());
		waitResponse();

		resizeColumn(column5);
		MatcherAssert.assertThat(jq("#zk_hdghost").offsetLeft(), greaterThan(column5.offsetLeft()));
	}

	@Test
	public void testTree() {
		connect();

		jq("html").scrollLeft(200);

		JQuery body = jq("@tree .z-tree-body");
		JQuery column5 = jq("@treecol:eq(4)");
		body.scrollLeft(body.scrollWidth());
		waitResponse();

		resizeColumn(column5);
		MatcherAssert.assertThat(jq("#zk_hdghost").offsetLeft(), greaterThan(column5.offsetLeft()));
	}

	private void resizeColumn(JQuery column) {
		int columnWidth = column.outerWidth();
		getActions().moveToElement(toElement(column))
				.moveByOffset(columnWidth / 2 - DRAGGING_THRESHOLD, 0)
				.clickAndHold()
				.moveByOffset(-100, 0)
				.perform();
	}
}
