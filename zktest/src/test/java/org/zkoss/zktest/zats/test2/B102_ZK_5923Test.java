/* B102_ZK_5923Test.java

	Purpose:
		
	Description:
		
	History:
		Tue Apr 15 11:25:24 CST 2025, Created by jameschu

Copyright (C) 2025 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

/**
 * @author jameschu
 */

public class B102_ZK_5923Test extends WebDriverTestCase {
	@Test
	public void test() throws Exception {
		connect();
		waitResponse();
		JQuery jqIntbox = jq("@intbox");
		click(jq("@grid .z-paging-next"));
		waitResponse();
		JQuery jqGridPageInput = jq("@grid .z-paging-input");
		JQuery jqLisboxPageInput = jq("@listbox .z-paging-input");
		JQuery jqTreePageInput = jq("@tree .z-paging-input");
		JQuery jqLastPagingPageInput = jq("@paging:last .z-paging-input");
		assertEquals("1", jqIntbox.val());
		assertEquals("2", jqGridPageInput.val());
		assertEquals("2", jqLisboxPageInput.val());
		assertEquals("2", jqTreePageInput.val());
		assertEquals("2", jqLastPagingPageInput.val());
		click(jq("@listbox .z-paging-next"));
		waitResponse();
		assertEquals("2", jqIntbox.val());
		assertEquals("3", jqGridPageInput.val());
		assertEquals("3", jqLisboxPageInput.val());
		assertEquals("3", jqTreePageInput.val());
		assertEquals("3", jqLastPagingPageInput.val());
		click(jq("@tree .z-paging-next"));
		waitResponse();
		assertEquals("3", jqIntbox.val());
		assertEquals("4", jqGridPageInput.val());
		assertEquals("4", jqLisboxPageInput.val());
		assertEquals("4", jqTreePageInput.val());
		assertEquals("4", jqLastPagingPageInput.val());
		click(jq("@paging:last .z-paging-next"));
		waitResponse();
		assertEquals("4", jqIntbox.val());
		assertEquals("5", jqGridPageInput.val());
		assertEquals("5", jqLisboxPageInput.val());
		assertEquals("5", jqTreePageInput.val());
		assertEquals("5", jqLastPagingPageInput.val());
	}
}
