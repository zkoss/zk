/* B95_ZK_3317Test.java

	Purpose:

	Description:

	History:
		Wed Dec 16 15:30:10 CST 2020, Created by katherinelin

Copyright (C) 2020 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.Test;
import org.zkoss.zktest.zats.WebDriverTestCase;
import org.zkoss.zktest.zats.ztl.JQuery;

import static org.junit.Assert.assertEquals;

public class B95_ZK_3317Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		waitResponse();
		JQuery listbox1 = jq("$box");
		JQuery listbox2 = jq("$box2");
		JQuery jqBar = jq(".z-listhead-bar");
		assertEquals(listbox1.find(".z-listcell-content:eq(0)").outerWidth() + jqBar.eq(0).outerWidth(),
				listbox1.width(), 1);
		listbox1.scrollTop(100);
		waitResponse();
		assertEquals(listbox1.find(".z-listcell-content:eq(0)").outerWidth() + jqBar.eq(0).outerWidth(),
			listbox1.width(), 1);
		assertEquals(listbox2.find(".z-listcell-content:eq(0)").outerWidth(), listbox2.width(), 1);
	}
}
