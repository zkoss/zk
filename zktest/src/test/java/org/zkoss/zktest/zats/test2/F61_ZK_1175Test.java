/* F61_ZK_1175Test.java

	Purpose:
		
	Description:
		
	History:
		Tue Apr 16 15:51:33 CST 2019, Created by rudyhuang

Copyright (C) 2019 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.Assert;
import org.junit.Test;

import org.zkoss.zktest.zats.WebDriverTestCase;
import org.zkoss.zktest.zats.ztl.JQuery;

/**
 * @author rudyhuang
 */
public class F61_ZK_1175Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();

		Assert.assertEquals(7, jq("@calendar:eq(0) th").length());
		Assert.assertEquals(8, jq("@calendar:eq(1) th").length());

		JQuery week = jq(".z-calendar-cell.z-calendar-weekofyear").first();
		click(week);
		waitResponse();
		Assert.assertEquals(week.text(), getMessageBoxContent());
		click(jq(".z-messagebox-button"));
		waitResponse();

		click(jq("@button"));
		waitResponse();
		Assert.assertEquals(7, jq("@calendar:eq(1) th").length());
		click(jq("@button"));
		waitResponse();
		Assert.assertEquals(8, jq("@calendar:eq(1) th").length());
	}

	@Test
	public void testDatebox() {
		connect();

		click(widget("@datebox:eq(0)").$n("btn"));
		waitResponse();
		Assert.assertEquals(7, jq(".z-datebox-popup.z-datebox-open @calendar:eq(0) th").length());

		click(widget("@datebox:eq(1)").$n("btn"));
		waitResponse();
		Assert.assertEquals(8, jq(".z-datebox-popup.z-datebox-open @calendar:eq(0) th").length());

		click(jq("@button:last"));
		waitResponse();
		click(widget("@datebox:eq(1)").$n("btn"));
		waitResponse();
		Assert.assertEquals(7, jq(".z-datebox-popup.z-datebox-open @calendar:eq(0) th").length());
	}
}
