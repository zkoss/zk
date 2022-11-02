/* ITreeitemRichletTest.java

	Purpose:

	Description:

	History:
		Mon Feb 21 15:41:57 CST 2022, Created by katherine

Copyright (C) 2022 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zephyr.webdriver.docs.data;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import org.zkoss.stateless.sul.ITreeitem;
import org.zkoss.test.webdriver.WebDriverTestCase;

/**
 * A set of unit test for {@link ITreeitem} Java Docs.
 * And also refers to something else on <a href="https://www.zkoss.org/wiki/ZK_Component_Reference/Data/Tree/Treeitem">Treeitem</a>,
 * if any.
 *
 * @author katherine
 * @see ITreeitem
 */
public class ITreeitemRichletTest extends WebDriverTestCase {
	@Test
	public void image() {
		connect("/data/itree/iTreeitem/image");
		assertTrue(jq(".z-treerow img").attr("src").contains("ZK-Logo.gif"));
	}

	@Test
	public void label() {
		connect("/data/itree/iTreeitem/label");
		assertTrue(jq(".z-treecell-text:contains(item 1)").exists());
	}

	@Test
	public void open() {
		connect("/data/itree/iTreeitem/open");
		assertEquals(3, jq(".z-treerow").length());
		click(jq("@button"));
		waitResponse();
		assertEquals(4, jq(".z-treerow").length());
	}

	@Test
	public void selectable() {
		connect("/data/itree/iTreeitem/selectable");
		click(jq(".z-treerow:eq(0)"));
		click(jq(".z-treerow:eq(1)"));
		waitResponse();
		assertTrue(jq(".z-treerow:eq(0)").hasClass("z-treerow-selected"));
		assertFalse(jq(".z-treerow:eq(1)").hasClass("z-treerow-selected"));
		click(jq("@button"));
		waitResponse();
		click(jq(".z-treerow:eq(1)"));
		assertTrue(jq(".z-treerow:eq(1)").hasClass("z-treerow-selected"));
	}

	@Test
	public void selected() {
		connect("/data/itree/iTreeitem/selected");
		assertFalse(jq(".z-treerow:eq(0)").hasClass("z-treerow-selected"));
		assertTrue(jq(".z-treerow:eq(1)").hasClass("z-treerow-selected"));
		click(jq("@button"));
		waitResponse();
		assertFalse(jq(".z-treerow:eq(1)").hasClass("z-treerow-selected"));
	}

	@Test
	public void children() {
		connect("/data/itree/iTreeitem/children");
		assertTrue(jq(".z-treecell-text:contains(item 1-1)").exists());
		click(jq(".z-icon-caret-down"));
		waitResponse();
		assertFalse(jq(".z-treecell-text:contains(item 1-1)").exists());
		assertTrue(jq(".z-treecell-text:contains(item 2)").exists());
	}

	@Test
	public void widthAndHflex() {
		connect("/data/itree/iTreeitem/widthAndHflex");
		click(jq("@button:eq(0)"));
		waitResponse();
		assertTrue(hasError());
		assertTrue(jq(".z-label:contains(readonly)").exists());
		click(jq(".z-messagebox-buttons button"));
		waitResponse();
		click(jq("@button:eq(1)"));
		waitResponse();
		assertTrue(hasError());
		assertTrue(jq(".z-label:contains(readonly)").exists());
	}
}