/* B90_ZK_4329Test.java

	Purpose:
		
	Description:
		
	History:
		Fri Oct 25 10:27:12 CST 2019, Created by rudyhuang

Copyright (C) 2019 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.Assert;
import org.junit.Test;

import org.zkoss.zktest.zats.WebDriverTestCase;

/**
 * @author rudyhuang
 */
public class B90_ZK_4329Test extends WebDriverTestCase {
	@Test
	public void testTreeModel() {
		connect();

		click(jq("@checkbox:eq(0)"));
		waitResponse();
		Assert.assertEquals(4, jq(".z-treerow-selected").length());

		click(widget("@treerow:eq(0)").$n("open"));
		waitResponse();
		Assert.assertEquals(7, jq(".z-treerow-selected").length());
	}

	@Test
	public void testListModel() {
		connect();

		click(jq("@checkbox:eq(1)"));
		waitResponse();
		Assert.assertNotEquals(0, jq("@listbox:eq(0)").find(".z-listitem-selected").length());
	}

	@Test
	public void testGroupsModel() {
		connect();

		click(jq("@checkbox:eq(2)"));
		waitResponse();
		Assert.assertNotEquals(0, jq("@listbox:eq(1)").find(".z-listitem-selected").length());
	}
}
