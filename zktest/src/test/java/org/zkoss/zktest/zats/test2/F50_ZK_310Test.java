/* F50_ZK_310Test.java

	Purpose:
		
	Description:
		
	History:
		Fri Apr 12 16:03:54 CST 2019, Created by rudyhuang

Copyright (C) 2019 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThan;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

/**
 * @author rudyhuang
 */
public class F50_ZK_310Test extends WebDriverTestCase {
	@Test
	public void testDynamicAddRemove() {
		connect();

		int gridWidthBefore = getGridFirstColumnWidth("grid");
		int grid2WidthBefore = getGridFirstColumnWidth("grid2");
		int grid3WidthBefore = getGridFirstColumnWidth("grid3");
		int grid4WidthBefore = getGridFirstColumnWidth("grid4");
		int grid5WidthBefore = getGridFirstColumnWidth("grid5");
		int listboxWidthBefore = getListboxFirstColumnWidth("listbox");
		int listbox2WidthBefore = getListboxFirstColumnWidth("listbox2");
		int listbox3WidthBefore = getListboxFirstColumnWidth("listbox3");
		int listbox4WidthBefore = getListboxFirstColumnWidth("listbox4");
		int listbox5WidthBefore = getListboxFirstColumnWidth("listbox5");
		int treeWidthBefore = getTreeFirstColumnWidth("tree");
		int tree2WidthBefore = getTreeFirstColumnWidth("tree2");
		int tree3WidthBefore = getTreeFirstColumnWidth("tree3");
		int tree4WidthBefore = getTreeFirstColumnWidth("tree4");
		int tree5WidthBefore = getTreeFirstColumnWidth("tree5");

		click(jq("@button:contains(Add items)"));
		waitResponse();
		click(jq("@button:contains(resize):first"));
		waitResponse();

		assertThat(getGridFirstColumnWidth("grid"), greaterThan(gridWidthBefore));
		assertThat(getGridFirstColumnWidth("grid2"), greaterThan(grid2WidthBefore));
		assertThat(getGridFirstColumnWidth("grid3"), greaterThan(grid3WidthBefore));
		assertThat(getGridFirstColumnWidth("grid4"), greaterThan(grid4WidthBefore));
		assertThat(getGridFirstColumnWidth("grid5"), greaterThan(grid5WidthBefore));
		assertThat(getListboxFirstColumnWidth("listbox"), greaterThan(listboxWidthBefore));
		assertThat(getListboxFirstColumnWidth("listbox2"), greaterThan(listbox2WidthBefore));
		assertThat(getListboxFirstColumnWidth("listbox3"), greaterThan(listbox3WidthBefore));
		assertThat(getListboxFirstColumnWidth("listbox4"), greaterThan(listbox4WidthBefore));
		assertThat(getListboxFirstColumnWidth("listbox5"), greaterThan(listbox5WidthBefore));
		assertThat(getTreeFirstColumnWidth("tree"), greaterThan(treeWidthBefore));
		assertThat(getTreeFirstColumnWidth("tree2"), greaterThan(tree2WidthBefore));
		assertThat(getTreeFirstColumnWidth("tree3"), greaterThan(tree3WidthBefore));
		assertThat(getTreeFirstColumnWidth("tree4"), greaterThan(tree4WidthBefore));
		assertThat(getTreeFirstColumnWidth("tree5"), greaterThan(tree5WidthBefore));

		click(jq("@button:contains(remove last)"));
		waitResponse();
		click(jq("@button:contains(resize):first"));
		waitResponse();
		Assertions.assertEquals(gridWidthBefore, getGridFirstColumnWidth("grid"));
		Assertions.assertEquals(grid2WidthBefore, getGridFirstColumnWidth("grid2"));
		Assertions.assertEquals(grid3WidthBefore, getGridFirstColumnWidth("grid3"));
		Assertions.assertEquals(grid4WidthBefore, getGridFirstColumnWidth("grid4"));
		Assertions.assertEquals(grid5WidthBefore, getGridFirstColumnWidth("grid5"));
		Assertions.assertEquals(listboxWidthBefore, getListboxFirstColumnWidth("listbox"));
		Assertions.assertEquals(listbox2WidthBefore, getListboxFirstColumnWidth("listbox2"));
		Assertions.assertEquals(listbox3WidthBefore, getListboxFirstColumnWidth("listbox3"));
		Assertions.assertEquals(listbox4WidthBefore, getListboxFirstColumnWidth("listbox4"));
		Assertions.assertEquals(listbox5WidthBefore, getListboxFirstColumnWidth("listbox5"));
		Assertions.assertEquals(treeWidthBefore, getTreeFirstColumnWidth("tree"));
		Assertions.assertEquals(tree5WidthBefore, getTreeFirstColumnWidth("tree2"), 1);
		Assertions.assertEquals(tree4WidthBefore, getTreeFirstColumnWidth("tree3"), 1);
		Assertions.assertEquals(tree3WidthBefore, getTreeFirstColumnWidth("tree4"), 1);
		Assertions.assertEquals(tree5WidthBefore, getTreeFirstColumnWidth("tree5"), 1);
	}

	private int getGridFirstColumnWidth(String id) {
		return jq("$" + id).find("@row:first .z-row-inner:first").outerWidth();
	}

	private int getListboxFirstColumnWidth(String id) {
		return jq("$" + id).find("@listcell:first").outerWidth();
	}

	private int getTreeFirstColumnWidth(String id) {
		return jq("$" + id).find("@treecell:first").outerWidth();
	}

	@Test
	public void testResizeHFlexMin() {
		connect();

		JQuery innerDiv = jq("@center > @div > @div");
		int width = innerDiv.outerWidth();
		click(jq("@button:contains(enlarge)"));
		waitResponse();
		assertThat(innerDiv.outerWidth(), greaterThan(width));

		click(jq("@button:contains(resize back)"));
		waitResponse();
		Assertions.assertEquals(width, innerDiv.outerWidth());
	}
}
