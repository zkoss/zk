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
		JQuery listbox = jq(".z-listbox");
		JQuery jqBar = jq(".z-listhead-bar");
		assertEquals(listbox.eq(0).find(".z-listcell-content").eq(0).outerWidth() + jqBar.eq(0).outerWidth(),
			listbox.eq(0).width());
		assertEquals(listbox.eq(1).find(".z-listcell-content").eq(0).outerWidth(), listbox.eq(1).width());
	}
}
