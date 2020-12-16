/* B95_ZK_3318Test.java

	Purpose:

	Description:

	History:
		Wed Dec 16 16:15:20 CST 2020, Created by katherinelin

Copyright (C) 2020 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.Test;
import org.zkoss.zktest.zats.WebDriverTestCase;
import org.zkoss.zktest.zats.ztl.JQuery;

import static org.junit.Assert.assertEquals;

public class B95_ZK_3318Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		waitResponse();
		JQuery content = jq(".z-focus-a");
		JQuery jqBar = jq(".z-treecols-bar");
		JQuery tree = jq(".z-tree");
		assertEquals(tree.eq(0).find(".z-treerow").eq(0).outerWidth() + content.eq(0).outerWidth()
			+ jqBar.eq(0).outerWidth(), tree.eq(0).width());
		assertEquals(tree.eq(1).find(".z-treerow").eq(0).outerWidth() + content.eq(1).outerWidth(),
			tree.eq(1).width());
	}
}
