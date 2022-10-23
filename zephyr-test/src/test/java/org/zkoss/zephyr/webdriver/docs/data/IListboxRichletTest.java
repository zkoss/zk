/* IListboxRichletTest.java

	Purpose:

	Description:

	History:
		Thu Apr 07 17:01:45 CST 2022, Created by katherine

Copyright (C) 2022 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zephyr.webdriver.docs.data;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Iterator;

import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

/**
 * A set of unit test for {@link org.zkoss.zephyr.zpr.IListbox} Java Docs.
 * And also refers to something else on <a href="https://www.zkoss.org/wiki/ZK_Component_Reference/Data/Listbox">Listbox</a>,
 * if any.
 *
 * @author katherine
 * @see org.zkoss.zephyr.zpr.IListbox
 */
public class IListboxRichletTest extends WebDriverTestCase {
	@Test
	public void paging() {
		connect("/data/ilistbox/paging");
		assertTrue(jq(".z-paging").exists());
		assertEquals(3, jq(".z-listitem").length());
		click(jq(".z-paging-icon.z-icon-angle-double-right"));
		assertEquals(1, jq(".z-listitem").length());
		click(jq(".z-paging-icon.z-icon-angle-left"));
		waitResponse();
		assertEquals(3, jq(".z-listitem").length());
	}

	@Test
	public void children() {
		connect("/data/ilistbox/children");
		assertEquals(2, jq(".z-auxhead").children(".z-auxheader").length());
		assertEquals("MEMBER", jq(".z-auxheader-content:eq(0)").text());
		assertEquals(3, jq(".z-listhead").children(".z-listheader").length());
		assertEquals("Name", jq(".z-listheader-content:eq(0)").text());
		assertEquals(4, jq(".z-listbox-body tbody").children(".z-listitem").length());
		assertEquals(3, jq(".z-listitem:eq(0)").children(".z-listcell").length());
		assertEquals("Mary", jq(".z-listcell-content:eq(0)").text());
		assertEquals(2, jq(".z-listfoot").children(".z-listfooter").length());
		assertEquals("This is footer1", jq(".z-listfooter-content:eq(0)").text());
		int frozenColumnWidth = jq(".z-listheader:eq(0)").outerWidth() + jq(".z-listheader:eq(1)").outerWidth();
		assertEquals(frozenColumnWidth, jq(".z-frozen-body").outerWidth(), 1);
	}

	@Test
	public void autoSort() {
		connect("/data/ilistbox/autosort");
		Iterator<JQuery> iterator = jq(".z-listitem").iterator();
		for (int i = 0; ++i <= 3 && iterator.hasNext();) {
			assertEquals(String.valueOf(i), iterator.next().text());
		}
	}

	@Test
	public void checkmark() {
		connect("/data/ilistbox/checkmark");
		assertTrue(jq(".z-listitem-radio").exists());
		click(jq("@button"));
		waitResponse();
		assertFalse(jq(".z-listitem-radio").exists());
	}

	@Test
	public void checkmarkDeselectOther() {
		connect("/data/ilistbox/checkmarkDeselectOther");
		click(jq(".z-listitem-checkable:eq(0)"));
		click(jq(".z-listitem:eq(1)"));
		waitResponse();
		assertFalse(jq(".z-listitem:eq(0)").hasClass("z-listitem-selected"));
	}

	@Test
	public void innerWidth() {
		connect("/data/ilistbox/innerWidth");
		assertEquals(100, jq(".z-listbox-body table").outerWidth());
		click(jq("@button"));
		waitResponse();
		assertEquals(200, jq(".z-listbox-body table").outerWidth());
	}

	@Test
	public void listgroupSelectable() {
		connect("/data/ilistbox/listgroupSelectable");
		assertTrue(jq(".z-listgroup-checkable").exists());
	}

	@Test
	public void multiple() {
		connect("/data/ilistbox/multiple");
		assertTrue(jq(".z-listitem-checkbox").exists());
		click(jq("@button"));
		waitResponse();
		assertTrue(jq(".z-listitem-radio").exists());
	}

	@Test
	public void nonselectableTags() {
		connect("/data/ilistbox/nonselectableTags");
		click(jq(".z-listitem"));
		waitResponse();
		assertFalse(jq(".z-listitem-selected").exists());
		click(jq("@button:contains(change nonselectableTags)"));
		waitResponse();
		click(jq(".z-listitem"));
		waitResponse();
		assertTrue(jq(".z-listitem-selected").exists());
	}

	@Test
	public void rightSelect() {
		connect("/data/ilistbox/rightSelect");
		rightClick(jq(".z-listitem"));
		assertFalse(jq(".z-listitem-selected").exists());
		click(jq("@button"));
		waitResponse();
		rightClick(jq(".z-listitem"));
		waitResponse();
		assertTrue(jq(".z-listitem-selected").exists());
	}

	@Test
	public void selectOnHighlightDisabled() {
		connect("/data/ilistbox/selectOnHighlightDisabled");
		assertEquals("true", WebDriverTestCase.getEval("zk.$('$listbox')._selectOnHighlightDisabled"));
		click(jq("@button"));
		waitResponse();
		assertEquals("false", WebDriverTestCase.getEval("zk.$('$listbox')._selectOnHighlightDisabled"));
	}

	@Test
	public void rows() {
		connect("/data/ilistbox/rows");
		int height = jq(".z-listbox-body").height() * 2;
		click(jq("@button"));
		waitResponse();
		assertEquals(height ,jq(".z-listbox-body").height(), 1);
	}
}