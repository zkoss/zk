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
		JQuery grid = jq(".z-grid");
		assertEquals(grid.eq(0).find(".z-row-content").eq(0).outerWidth() + jqBar.eq(0).outerWidth(), grid.eq(0).width());
		assertEquals(grid.eq(1).find(".z-row-content").eq(0).outerWidth(), jq(".z-grid").eq(1).width());
	}
}
