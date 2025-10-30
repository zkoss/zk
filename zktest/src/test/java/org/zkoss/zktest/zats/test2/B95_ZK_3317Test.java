/* B95_ZK_3317Test.java

	Purpose:

	Description:

	History:
		Wed Dec 16 15:30:10 CST 2020, Created by katherinelin

Copyright (C) 2020 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

public class B95_ZK_3317Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		waitResponse();
		JQuery listbox1 = jq("$box");
		JQuery listbox2 = jq("$box2");
		JQuery listbox3 = jq("$box3");
		JQuery jqBar = jq(".z-listhead-bar");
		assertEquals(listbox1.find(".z-listcell-content:eq(0)").outerWidth() + jqBar.eq(0).outerWidth(),
				listbox1.width(), 1);
		listbox1.scrollTop(100);
		waitResponse();
		assertEquals(listbox1.find(".z-listcell-content:eq(0)").outerWidth() + jqBar.eq(0).outerWidth(),
			listbox1.width(), 1);
		assertEquals(listbox2.find(".z-listcell-content:eq(0)").outerWidth(), listbox2.width(), 1);
		assertNotEquals(listbox3.find(".z-rows").height(), listbox3.find(".z-listbox-body").height(), 1);
	}
}
