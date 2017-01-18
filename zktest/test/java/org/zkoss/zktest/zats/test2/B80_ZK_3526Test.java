/* B80_ZK_3526Test.java

	Purpose:

	Description:

	History:
		Wed Jan 11 14:34:22 CST 2017, Created by jameschu

Copyright (C) 2017 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.openqa.selenium.Keys;

import org.zkoss.zktest.zats.WebDriverTestCase;
import org.zkoss.zktest.zats.ztl.JQuery;

/**
 *
 * @author jameschu
 */
public class B80_ZK_3526Test extends WebDriverTestCase{
    @Test
    public void test() {
        connect();
		JQuery grid = jq("@grid");
		JQuery listbox = jq("@listbox");
		JQuery addBtn = jq("@button").eq(0);
		JQuery clearBtn = jq("@button").eq(1);
		click(addBtn);
		waitResponse();
		assertEquals(2, grid.find("@row").length());
		assertEquals(2, listbox.find("@listitem").length());
		click(addBtn);
		waitResponse();
		click(addBtn);
		waitResponse();
		assertEquals(4, grid.find("@row").length());
		assertEquals(4, listbox.find("@listitem").length());
		click(clearBtn);
		waitResponse();
		assertEquals(0, grid.find("@row").length());
		assertEquals(0, listbox.find("@listitem").length());
		click(addBtn);
		waitResponse();
		assertEquals(1, grid.find("@row").length());
		assertEquals(1, listbox.find("@listitem").length());
    }
}
