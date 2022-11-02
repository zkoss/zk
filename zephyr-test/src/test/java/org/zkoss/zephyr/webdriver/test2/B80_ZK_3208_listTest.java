/* B80_ZK_3208_listTest.java

	Purpose:
		
	Description:
		
	History:
		Tue, May 17, 2016  6:45:14 PM, Created by Christopher

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
public class B80_ZK_3208_listTest extends ClientBindTestCase {
	@Test
	public void test() {
		connect();
		sleep(2000);
		JQuery lb1 = jq("$lb1");
		JQuery lb2 = jq("$lb2");
		JQuery pg = jq("$myPaging");

		//check init status
		assertEquals(5, lb1.toWidget().get("pageSize"));
		assertEquals(5, lb2.toWidget().get("pageSize"));
		assertEquals(5, pg.toWidget().get("pageSize"));
		;

		assertEquals(0, lb1.toWidget().get("activePage"));
		assertEquals(0, lb2.toWidget().get("activePage"));
		assertEquals(0, pg.toWidget().get("activePage"));

		//switch to 2nd page
		click(jq(".z-paging-next"));
		waitResponse();

		//check result after switching page
		assertEquals(5, lb1.toWidget().get("pageSize"));
		assertEquals(5, lb2.toWidget().get("pageSize"));
		assertEquals(5, pg.toWidget().get("pageSize"));

		assertEquals(1, lb1.toWidget().get("activePage"));
		assertEquals(1, lb2.toWidget().get("activePage"));
		assertEquals(1, pg.toWidget().get("activePage"));

		//switch back for 1st page
		click(jq(".z-paging-previous"));
		waitResponse();

		//check result after switching page
		assertEquals(5, lb1.toWidget().get("pageSize"));
		assertEquals(5, lb2.toWidget().get("pageSize"));
		assertEquals(5, pg.toWidget().get("pageSize"));
		;

		assertEquals(0, lb1.toWidget().get("activePage"));
		assertEquals(0, lb2.toWidget().get("activePage"));
		assertEquals(0, pg.toWidget().get("activePage"));
	}
}
