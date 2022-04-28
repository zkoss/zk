/* B96_ZK_5117Test.java

	Purpose:
		
	Description:
		
	History:
		Thu Apr 28 15:27:36 CST 2022, Created by jameschu

Copyright (C) 2022 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import org.zkoss.zktest.zats.WebDriverTestCase;
import org.zkoss.zktest.zats.ztl.JQuery;

/**
 * @author jameschu
 */
public class B96_ZK_5117Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		JQuery sortColumn = jq(".z-treecol-sort:eq(1)");
		click(sortColumn);
		waitResponse();
		click(sortColumn);
		waitResponse();
		assertFalse(jq("span:contains(Item 7.1)").exists());
		click(jq(".z-tree-close"));
		waitResponse();
		assertTrue(jq("span:contains(Item 7.1)").exists());
		click(jq(".z-tree-open"));
		waitResponse();
		assertFalse(jq("span:contains(Item 7.1)").exists());
	}
}