/* B80_ZK_3308Test.java

	Purpose:
		
	Description:
		
	History:
		Thu Jan 12 15:00:31 CST 2017, Created by jameschu

Copyright (C) 2017 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import org.openqa.selenium.Keys;
import org.zkoss.zktest.zats.WebDriverTestCase;
import org.zkoss.zktest.zats.ztl.JQuery;

/**
 * @author jameschu
 */
public class B80_ZK_3308Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		JQuery grid = jq("@grid");
		JQuery lb = jq("@listbox");
		JQuery btn1 = jq("@button").eq(0);
		JQuery btn2 = jq("@button").eq(1);

		assertEquals(3, grid.find(".z-icon-angle-down").length());
		assertEquals(3, lb.find(".z-icon-angle-down").length());

		click(btn1);
		waitResponse();
		assertEquals(0, grid.find(".z-icon-angle-down").length());
		assertEquals(3, grid.find(".z-icon-angle-right").length());
		click(btn1);
		waitResponse();
		assertEquals(3, grid.find(".z-icon-angle-down").length());
		assertEquals(0, grid.find(".z-icon-angle-right").length());

		click(btn2);
		waitResponse();
		assertEquals(0, lb.find(".z-icon-angle-down").length());
		assertEquals(3, lb.find(".z-icon-angle-right").length());
		click(btn2);
		waitResponse();
		assertEquals(3, lb.find(".z-icon-angle-down").length());
		assertEquals(0, lb.find(".z-icon-angle-right").length());

	}
}
