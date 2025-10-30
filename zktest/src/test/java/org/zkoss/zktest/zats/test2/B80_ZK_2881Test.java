/* B80_ZK_2881Test.java

	Purpose:
		
	Description:
		
	History:
		Wed Apr 10 11:10:18 CST 2019, Created by rudyhuang

Copyright (C) 2019 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;

/**
 * @author rudyhuang
 */
public class B80_ZK_2881Test extends WebDriverTestCase {
	@Test
	public void testListbox() {
		connect();

		click(jq("@listitem:eq(1)"));
		click(jq("@listitem:eq(2)"));
		click(jq("@listitem:eq(3)"));
		waitResponse();
		click(jq("@listitem:eq(3)"));
		waitResponse();
		Assertions.assertEquals(jq("@listitem:eq(3)").offsetTop(), driver.switchTo().activeElement().getLocation().getY(), 1);
	}

	@Test
	public void testTree() {
		connect();

		click(jq("@treerow:eq(1) @treecell"));
		click(jq("@treerow:eq(2) @treecell"));
		click(jq("@treerow:eq(3) @treecell"));
		waitResponse();
		click(jq("@treerow:eq(3) @treecell"));
		waitResponse();
		Assertions.assertEquals(jq("@treerow:eq(3)").offsetTop(), driver.switchTo().activeElement().getLocation().getY(), 1);
	}
}
