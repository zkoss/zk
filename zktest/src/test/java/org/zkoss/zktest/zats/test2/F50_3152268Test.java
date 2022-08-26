/* F50_3152268Test.java

	Purpose:
		
	Description:
		
	History:
		Tue Apr 09 18:11:28 CST 2019, Created by rudyhuang

Copyright (C) 2019 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

/**
 * @author rudyhuang
 */
public class F50_3152268Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();

		crudTreeItem(jq("@treerow:eq(0)"), 1);
		crudTreeItem(jq("@treerow:eq(0)"), 0);
		Assertions.assertEquals(4, jq("@treerow").length());
		Assertions.assertEquals("new treeitem 1", trimNbsp(jq("@treecell:eq(0)").text()));

		crudTreeItem(jq("@treerow:eq(0)"), 2);
		Assertions.assertEquals("update treeitem 2", trimNbsp(jq("@treecell:eq(0)").text()));

		crudTreeItem(jq("@treerow:eq(1)"), 3);
		Assertions.assertEquals(3, jq("@treerow").length());
	}

	private void crudTreeItem(JQuery treeitem, int menuitemIndex) {
		rightClick(treeitem);
		waitResponse();
		click(jq(String.format("@menuitem:visible:eq(%d)", menuitemIndex)));
		waitResponse();
	}

	private String trimNbsp(String text) {
		return text.replaceAll("\u00A0", "").trim();
	}
}
