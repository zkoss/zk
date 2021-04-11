/* B96_ZK_4846Test.java

	Purpose:
		
	Description:
		
	History:
		Fri Apr 9 11:50:11 CST 2021, Created by jameschu

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

import org.zkoss.zktest.zats.WebDriverTestCase;
import org.zkoss.zktest.zats.ztl.JQuery;

/**
 * @author jameschu
 */

public class B96_ZK_4846Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		waitResponse();
		JQuery jqListitem = jq("@listitem");
		for (int i = 0; i < jqListitem.length(); i++) {
			click(jqListitem.eq(i));
		}
		waitResponse();
		assertEquals(1, jq(".z-listheader-checked").length());
		click(jq(".z-listheader-checkable"));
		JQuery jqTreerow = jq("@treerow");
		for (int i = 0; i < jqTreerow.length(); i++) {
			click(jqTreerow.eq(i));
		}
		waitResponse();
		assertEquals(1, jq(".z-treecol-checked").length());
		click(jq(".z-treecol-checkable"));
		waitResponse();
		assertEquals(0, jq(".z-listheader-checked").length());
		assertEquals(0, jq(".z-treecol-checked").length());
	}
}
