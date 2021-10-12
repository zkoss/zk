/* B85_ZK_3696Test.java

	Purpose:
		
	Description:
		
	History:
		Mon Jul 10 15:24:13 CST 2017, Created by rudyhuang

Copyright (C) 2017 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.Assert;
import org.junit.Test;

import org.zkoss.zktest.zats.WebDriverTestCase;
import org.zkoss.zktest.zats.ztl.Element;
import org.zkoss.zktest.zats.ztl.JQuery;

/**
 * @author rudyhuang
 */
public class B85_ZK_3696Test extends WebDriverTestCase {
	@Test
	public void testDynamic() {
		connect();
		sleep(1000);
		Element headrows = jq("@grid:eq(0)").toWidget().$n("headrows");
		JQuery visible = jq(headrows).find("tr:visible");
		Assert.assertEquals("There are hidden headers not displayed properly.", 5, visible.length());
	}

	@Test
	public void testStatic() {
		connect();
		sleep(1000);
		Element headrows = jq("@grid:eq(1)").toWidget().$n("headrows");
		JQuery visible = jq(headrows).find("tr:visible");
		Assert.assertEquals("There are hidden headers not displayed properly.", 5, visible.length());
	}
}
