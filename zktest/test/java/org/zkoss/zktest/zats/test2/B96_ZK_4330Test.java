/* B96_ZK_4330Test.java

	Purpose:

	Description:

	History:
		Tue Feb 09 20:30:19 CST 2021, Created by katherinelin

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.Assert;
import org.junit.Test;
import org.zkoss.zktest.zats.WebDriverTestCase;
import org.zkoss.zktest.zats.ztl.JQuery;


public class B96_ZK_4330Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		JQuery selCountBtn = jq("@button:eq(2)");
		JQuery selAllBtn = jq("@button:eq(0)");
		click(selAllBtn);
		click(selCountBtn);
		click(jq("@button:eq(3)"));
		click(selCountBtn);
		click(jq("@button:eq(4)"));
		waitResponse();
		Assert.assertFalse(jq(".z-treecol-checkable:eq(0)").hasClass("z-treecol-checked"));
		Assert.assertFalse(jq(".z-treecol-checkable:eq(1)").hasClass("z-treecol-checked"));
		Assert.assertFalse(jq(".z-treecol-checkable:eq(2)").hasClass("z-treecol-checked"));
		click(selAllBtn);
		click(selCountBtn);
		click(jq("@button:eq(1)"));
		click(jq("@button:eq(5)"));
		click(selAllBtn);
		click(selCountBtn);
		waitResponse();
		Assert.assertEquals("6, 400, 5\n4, 398, 4\n6, 400, 5\n6, 400, 5", getZKLog());
	}
}
