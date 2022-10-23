/* Issue0072CRUDModelTest.java

	Purpose:
		
	Description:
		
	History:
		12:24 PM 2021/11/5, Created by jumperchen

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zephyr.webdriver.issues;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;

/**
 * @author jumperchen
 */
public class Issue0072CRUDModelTest  extends WebDriverTestCase {
	@Test
	public void testCRUDModel() {
		connect("/issues/issue_0072_crud_model.zpr");

		assertEquals("[]", jq("@treeitem:nth-child(2)").text());

		// insert
		click(jq(".z-button:contains(\"insert\")"));
		waitResponse();
		assertEquals("[aaa]", jq("@treeitem:nth-child(2)").text());
		assertEquals(1, jq("@treeitem:nth-child(2) .z-tree-icon").length());

		click(jq("@treeitem:nth-child(2) .z-tree-icon"));
		waitResponse();
		assertEquals("aaa", jq("@treeitem:nth-child(2) > @treechildren > @treeitem").text());

		click(jq(".z-button:contains(\"tree invalidate\")"));
		waitResponse();
		assertEquals("[aaa]", jq("@treeitem:nth-child(2)").text());
		assertEquals(1, jq("@treeitem:nth-child(2) .z-tree-icon").length());
		assertEquals("aaa", jq("@treeitem:nth-child(2) > @treechildren > @treeitem").text());

		click(jq("@treeitem:nth-child(2) > @treechildren > @treeitem"));
		waitResponse();
		assertEquals(1, jq("@treeitem:nth-child(2) > @treechildren > @treeitem.z-treerow-selected").length());

		// update
		type(jq(".z-textbox"), "bbb");
		click(jq(".z-button:contains(\"update the selected item\")"));
		waitResponse();
		assertEquals("bbb", jq("@treeitem:nth-child(2) > @treechildren > @treeitem").text());
		assertEquals(1, jq("@treeitem:nth-child(2) > @treechildren > @treeitem.z-treerow-selected").length());

		// remove
		click(jq(".z-button:contains(\"remove the selected item\")"));
		waitResponse();
		assertEquals(0, jq("@treeitem:nth-child(2) > @treechildren > @treeitem.z-treerow-selected").length());
		assertEquals(0, jq("@treeitem:nth-child(2) > @treechildren > @treeitem").length());
	}
}
