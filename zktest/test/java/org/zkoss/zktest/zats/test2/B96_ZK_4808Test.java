/* B96_ZK_4808Test.java

	Purpose:
		
	Description:
		
	History:
		Wed Mar 10 14:21:03 CST 2021, Created by katherinelin

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.Assert;
import org.junit.Test;
import org.zkoss.zktest.zats.WebDriverTestCase;
import org.zkoss.zktest.zats.ztl.JQuery;

public class B96_ZK_4808Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		JQuery topMenu = jq(".z-menu:eq(0)");
		JQuery nestedMenu = jq(".z-menu:eq(1)");
		click(topMenu.find(".z-menu-icon"));
		click(nestedMenu.find(".z-menu-text"));
		waitResponse();
		Assert.assertTrue(isZKLogAvailable());

		closeZKLog();
		click(nestedMenu.find(".z-menu-icon"));
		waitResponse();
		Assert.assertTrue(isZKLogAvailable());
	}
}
