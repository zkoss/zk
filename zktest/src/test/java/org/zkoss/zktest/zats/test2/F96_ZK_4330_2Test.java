/* F96_ZK_4330Test.java

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


public class F96_ZK_4330_2Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect("/test2/F96-ZK-4330.zul");
		JQuery tree1 = jq("@tree:eq(0)");
		JQuery tree2 = jq("@tree:eq(1)");
		JQuery tree3 = jq("@tree:eq(2)");
		JQuery selCountBtn = jq("@button:eq(2)");
		JQuery selAllBtn = jq("@button:eq(0)");
		click(jq("@button:eq(3)"));
		waitResponse();
		click(selAllBtn);
		click(selCountBtn);

		click(jq("@button:eq(4)"));
		waitResponse();
		click(selAllBtn);
		click(selCountBtn);
		Assert.assertFalse(tree1.find(".z-treecol-checkable").hasClass("z-treecol-checked"));
		Assert.assertFalse(tree2.find(".z-treecol-checkable").hasClass("z-treecol-checked"));
		Assert.assertFalse(tree3.find(".z-treecol-checkable").hasClass("z-treecol-checked"));
		click(jq("@button:eq(1)"));
		click(jq("@button:eq(5)"));
		click(selAllBtn);
		click(selCountBtn);
		waitResponse();
		Assert.assertEquals("2, 58, 1\n4, 60, 2\n4, 60, 2", getZKLog());
	}
}
