/* B85_ZK_3912Test.java

	Purpose:
		
	Description:
		
	History:
		Thu Jun 14 12:37:25 CST 2018, Created by rudyhuang

Copyright (C) 2018 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import org.junit.Test;

import org.zkoss.zktest.zats.WebDriverTestCase;
import org.zkoss.zktest.zats.ztl.JQuery;

/**
 * @author rudyhuang
 */
public class B85_ZK_3912Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();

		JQuery tab = jq("@tab");
		assertEquals("HELLO", tab.attr("title"));
		assertNotEquals("TAB", tab.find(".z-tab-text").attr("title"));
	}
}
