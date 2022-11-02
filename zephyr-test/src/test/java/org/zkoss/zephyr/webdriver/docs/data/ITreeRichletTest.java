/* ITreeRichletTest.java

	Purpose:

	Description:

	History:
		Wed Feb 16 16:53:38 CST 2022, Created by katherine

Copyright (C) 2022 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zephyr.webdriver.docs.data;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Iterator;

import org.junit.jupiter.api.Test;

import org.zkoss.stateless.sul.ITree;
import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

/**
 * A set of unit test for {@link ITree} Java Docs.
 * And also refers to something else on <a href="https://www.zkoss.org/wiki/ZK_Component_Reference/Data/Tree">Tree</a>,
 * if any.
 * @see ITree
 * @author katherine
 */
public class ITreeRichletTest extends WebDriverTestCase {
	@Test
	public void defaultSelection() {
		connect("/data/itree/defaultSelection");
		assertTrue(jq(".z-treerow:eq(2)").hasClass("z-treerow-selected"));
	}

	@Test
	public void twoColumnTree() {
		connect("/data/itree/mold/default");
		assertEquals(2, jq(".z-treecol").length());
		assertEquals(2, jq(".z-treerow:eq(0) .z-treecell").length());
		assertEquals(2, jq(".z-treefooter").length());
		assertTrue(jq(".z-treerow:eq(1) .z-icon-caret-down").hasClass("z-icon-caret-down z-tree-open"));
	}

	@Test
	public void paging() {
		connect("/data/itree/mold/paging/test");
		assertTrue(jq(".z-paging").exists());
		//page 1 has three treecell, page 2 has two tree cell.
		assertEquals(3, jq(".z-treerow").length());
		click(jq(".z-paging-icon.z-icon-angle-double-right"));
		assertEquals(2, jq(".z-treerow").length());
		click(jq(".z-paging-icon.z-icon-angle-left"));
		waitResponse();
		assertEquals(3, jq(".z-treerow").length());
	}

	@Test
	public void checkmark() {
		connect("/data/itree/checkmark");
		assertTrue(jq(".z-treerow-radio").exists());
		click(jq("@button"));
		waitResponse();
		assertFalse(jq(".z-treerow-radio").exists());
	}

	@Test
	public void checkmarkDeselectOther() {
		connect("/data/itree/checkmarkDeselectOther");
		click(jq(".z-treerow:eq(0)"));
		click(jq(".z-treerow:eq(1)"));
		waitResponse();
		assertFalse(jq(".z-treerow-icon.z-icon-check:eq(0)").isVisible());
		click(jq("@button"));
		waitResponse();
		click(jq(".z-treerow:eq(0)"));
		assertTrue(jq(".z-treerow-icon.z-icon-check:eq(1)").isVisible());
	}

	@Test
	public void frozen() {
		connect("/data/itree/frozen");
		assertTrue(jq(".z-frozen").exists());
		int col1OffsetLeft = jq(".z-treecol:eq(0)").offsetLeft();
		int col2OffsetLeft = jq(".z-treecol:eq(1)").offsetLeft();
		JQuery frozenInner = jq(".z-frozen-inner").eq(0);
		frozenInner.scrollLeft(200);
		waitResponse();
		assertEquals(col1OffsetLeft, jq(".z-treecol:eq(0)").offsetLeft());
		assertEquals(col2OffsetLeft, jq(".z-treecol:eq(1)").offsetLeft());
	}

	@Test
	public void multiple() {
		connect("/data/itree/multiple");
		assertTrue(jq(".z-treerow-checkbox").exists());
		click(jq("@button"));
		waitResponse();
		assertTrue(jq(".z-treerow-radio").exists());
	}

	@Test
	public void nonselectableTags() {
		connect("/data/itree/nonselectableTags");
		JQuery treerow = jq(".z-treerow");
		click(treerow);
		waitResponse();
		assertTrue(treerow.hasClass("z-treerow-selected"));
		click(jq("@button"));
		waitResponse();
		JQuery treerow2 = jq(".z-treerow:eq(1)");
		click(treerow2);
		waitResponse();
		assertFalse(treerow2.hasClass("z-treerow-selected"));
	}

//	Disable in Jenkins', due to this case cannot work on linux env.
//	@Test
//	public void selectOnHighlightDisabled() {
//		connect("/data/itree/selectOnHighlightDisabled");
//		JQuery treerow = jq(".z-treerow");
//		WebElement element = toElement(treerow);
//		Actions actions = getActions();
//		actions.moveToElement(element, 10, 5)
//				.clickAndHold()
//				.moveByOffset(50, 5)
//				.release()
//				.perform();
//		waitResponse();
//		assertFalse(treerow.hasClass("z-treerow-selected"));
//		click(jq("@button"));
//		waitResponse();
//
//		actions.moveToElement(element, 10, 5)
//				.clickAndHold()
//				.moveByOffset(50, 5)
//				.release()
//				.perform();
//		assertTrue(treerow.hasClass("z-treerow-selected"));
//	}

	@Test
	public void rightSelect() {
		connect("/data/itree/rightSelect");
		JQuery treerow = jq(".z-treerow");
		rightClick(treerow);
		waitResponse();
		assertTrue(treerow.hasClass("z-treerow-selected"));
		click(jq("@button"));
		waitResponse();
		JQuery treerow2 = jq(".z-treerow:eq(1)");
		rightClick(treerow2);
		assertFalse(treerow2.hasClass("z-treerow-selected"));
	}

	@Test
	public void scrollable() {
		connect("/data/itree/scrollable");
		JQuery treeBody = jq(".z-tree-body");
		treeBody.scrollTop(100);
		waitResponse();
		assertEquals(100, treeBody.scrollTop());
	}

	@Test
	public void createOnOpen() {
		connect("/data/itree/createOnOpen");
		click(jq(".z-icon-caret-right"));
		waitResponse();

		assertEquals("New added", jq(".z-treecell-text:eq(1)").text());
	}

	@Test
	public void sort() {
		connect("/data/itree/sort");
		Iterator<JQuery> iterator = jq(".z-treechildren tr").iterator();
		iterator.hasNext();
		assertEquals("Visible ROOT", iterator.next().text());
		for (int i = 0; i < 10 && iterator.hasNext(); i++) {
			assertEquals(String.valueOf(i), iterator.next().text());
		}

		// test ascending
		clickAt(jq(".z-treecol-content"), 5, 5);
		waitResponse();
		iterator = jq(".z-treechildren tr").iterator();
		iterator.hasNext();
		for (int i = 0; i < 10 && iterator.hasNext(); i++) {
			assertEquals(String.valueOf(i), iterator.next().text());
		}
		assertEquals("Visible ROOT", iterator.next().text());

		// test descending
		clickAt(jq(".z-treecol-content"), 5, 5);
		waitResponse();
		iterator = jq(".z-treechildren tr").iterator();
		iterator.hasNext();
		assertEquals("Visible ROOT", iterator.next().text());
		for (int i = 10; --i > 0 && iterator.hasNext();) {
			assertEquals(String.valueOf(i), iterator.next().text());
		}
	}

	@Test
	public void autoSort() {
		connect("/data/itree/autoSort");
		Iterator<JQuery> iterator = jq(".z-treechildren tr").iterator();
		iterator.hasNext();
		assertEquals("Visible ROOT", iterator.next().text());
		for (int i = 10; --i > 0 && iterator.hasNext();) {
			assertEquals(String.valueOf(i), iterator.next().text());
		}
	}

	@Test
	public void children() {
		connect("/data/itree/children");
		assertTrue(jq(".z-auxhead .z-auxheader").exists());
		assertEquals(2, jq(".z-treecols .z-treecol").length());
		assertEquals(7, jq(".z-treechildren .z-treerow").length());
		assertEquals(2, jq(".z-treefoot .z-treefooter").length());
	}

	@Test
	public void heightAndRows() {
		connect("/data/itree/heightAndRows");
		click(jq("@button"));
		waitResponse();
		assertTrue(hasError());
		assertTrue(jq(".z-label:contains(Not allowed to set height and rows at the same time)").exists());
	}
}