/* B80_ZK_2928Test.java

	Purpose:
		
	Description:
		
	History:
		11:42 AM 12/22/15, Created by jumperchen

Copyright (C) 2015 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zephyr.webdriver.test2;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import org.zkoss.zephyr.webdriver.ZephyrClientMVVMTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;
import org.zkoss.test.webdriver.ztl.Widget;

/**
 * @author jumperchen
 */
public class B80_ZK_2928Test extends ZephyrClientMVVMTestCase {
	@Test
	public void testZK2928() {
		connect();
		JQuery jq = jq("@button");
		click(jq);
		waitResponse();
		assertEquals("showSelectedItem:null", getZKLog());
		closeZKLog();

		Widget widget = selectComboitem(jq("@combobox").toWidget(), 0);
		String label = widget.get("label");
		waitResponse();
		assertEquals(">>" +label, getZKLog());
		closeZKLog();

		click(jq);
		waitResponse();
		assertEquals("showSelectedItem:" + label, getZKLog());
		closeZKLog();

		type(jq("@combobox").find(".z-combobox-input"), "");
		waitResponse();
		assertEquals(">>null", getZKLog());
		closeZKLog();

		click(jq);
		waitResponse();
		assertEquals("showSelectedItem:null", getZKLog());
	}
}
