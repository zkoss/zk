/* B95_ZK_3316Test.java

	Purpose:

	Description:

	History:
		Wed Dec 16 16:12:32 CST 2020, Created by katherinelin

Copyright (C) 2020 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.Test;
import org.zkoss.zktest.zats.WebDriverTestCase;
import org.zkoss.zktest.zats.ztl.JQuery;

import static org.junit.Assert.assertEquals;

public class B95_ZK_3316Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		waitResponse();
		JQuery jqBar = jq(".z-columns-bar");
		JQuery grid1 = jq("$grid1");
		JQuery grid2 = jq("$grid2");
		assertEquals(grid1.find(".z-row-content:eq(0)").outerWidth() + jqBar.outerWidth(), grid1.width());
		grid1.scrollTop(100);
		waitResponse();
		assertEquals(grid1.find(".z-row-content:eq(0)").outerWidth() + jqBar.outerWidth(), grid1.width());
		assertEquals(grid2.find(".z-row-content:eq(0)").outerWidth(), grid2.width());
	}
}
