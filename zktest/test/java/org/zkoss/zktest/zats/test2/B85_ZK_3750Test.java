/* B85_ZK_3750Test.java

	Purpose:
		
	Description:
		
	History:
		Mon Nov 06 15:55:15 CST 2017, Created by rudyhuang

Copyright (C) 2017 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import org.zkoss.zktest.zats.WebDriverTestCase;
import org.zkoss.zktest.zats.ztl.JQuery;

/**
 * @author rudyhuang
 */
public class B85_ZK_3750Test extends WebDriverTestCase {
	@Test
	public void test() throws Exception {
		connect();
		assertEquals("right", jq(".z-column:eq(2)").css("text-align"));
		assertEquals("right", jq(".z-footer:eq(2)").css("text-align"));

		JQuery rows = jq(".z-row");
		for (int i = 0; i < rows.length(); i++) {
			assertEquals("right", rows.eq(i).find(".z-row-inner:eq(2)").css("text-align"));
		}
	}
}
