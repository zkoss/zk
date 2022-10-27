/* B80_ZK_3208_treeTest.java

	Purpose:
		
	Description:
		
	History:
		Tue, May 17, 2016  6:45:29 PM, Created by Christopher

Copyright (C) 2016 Potix Corporation. All Rights Reserved.

This program is distributed under LGPL Version 2.1 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
package org.zkoss.zephyr.webdriver.test2;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import org.zkoss.zephyr.webdriver.ClientBindTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

/**
 * @author Christopher
 */
public class B80_ZK_3208_treeTest extends ClientBindTestCase {
	@Test
	public void test() {
		connect();
		sleep(2000);
		JQuery tree1 = jq("$tree1");
		JQuery tree2 = jq("$tree2");
		JQuery pg = jq("$myPaging");

		//check init status
		assertEquals(5, tree1.toWidget().get("pageSize"));
		assertEquals(5, tree2.toWidget().get("pageSize"));
		assertEquals(5, pg.toWidget().get("pageSize"));

		assertEquals(0, tree1.toWidget().get("activePage"));
		assertEquals(0, tree2.toWidget().get("activePage"));
		assertEquals(0, pg.toWidget().get("activePage"));

		//switch to 2nd page
		click(jq(".z-paging-next"));
		waitResponse();

		//check result after switching page
		assertEquals(5, tree1.toWidget().get("pageSize"));
		assertEquals(5, tree2.toWidget().get("pageSize"));
		assertEquals(5, pg.toWidget().get("pageSize"));

		assertEquals(1, tree1.toWidget().get("activePage"));
		assertEquals(1, tree2.toWidget().get("activePage"));
		assertEquals(1, pg.toWidget().get("activePage"));

		//switch back for 1st page
		click(jq(".z-paging-previous"));
		waitResponse();

		//check result after switching page
		assertEquals(5, tree1.toWidget().get("pageSize"));
		assertEquals(5, tree2.toWidget().get("pageSize"));
		assertEquals(5, pg.toWidget().get("pageSize"));

		assertEquals(0, tree1.toWidget().get("activePage"));
		assertEquals(0, tree2.toWidget().get("activePage"));
		assertEquals(0, pg.toWidget().get("activePage"));
	}
}
