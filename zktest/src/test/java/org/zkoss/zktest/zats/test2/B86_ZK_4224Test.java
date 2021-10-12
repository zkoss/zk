/* B86_ZK_4224Test.java

	Purpose:
		
	Description:
		
	History:
		Tue May 07 15:44:32 CST 2019, Created by rudyhuang

Copyright (C) 2019 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.Assert;
import org.junit.Test;

import org.zkoss.zktest.zats.WebDriverTestCase;
import org.zkoss.zktest.zats.ztl.JQuery;
import org.zkoss.zktest.zats.ztl.Widget;

/**
 * @author rudyhuang
 */
public class B86_ZK_4224Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();

		checkSortIcon(widget("@listbox:eq(0) @listheader:first"));
		checkSortIcon(widget("@grid:eq(0) @column:first"));
		checkSortIcon(widget("@tree:eq(0) @treecol:first"));
		checkSortIcon(widget("@listbox:eq(1) @listheader:first"));
		checkSortIcon(widget("@grid:eq(1) @column:first"));
		checkSortIcon(widget("@tree:eq(1) @treecol:first"));
	}

	private void checkSortIcon(Widget column) {
		JQuery sortIcon = jq(column.$n("sort-icon"));
		Assert.assertNotEquals("null", sortIcon.attr("class"));
	}
}
