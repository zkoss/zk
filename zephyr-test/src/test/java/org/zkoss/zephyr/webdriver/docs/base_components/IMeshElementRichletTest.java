/* IMeshElementRichletTest.java

	Purpose:

	Description:

	History:
		Wed Feb 23 12:36:45 CST 2022, Created by katherine

Copyright (C) 2022 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zephyr.webdriver.docs.base_components;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import org.zkoss.stateless.zpr.IMeshElement;
import org.zkoss.test.webdriver.WebDriverTestCase;

/**
 * A set of unit test for {@link IMeshElement} Java Docs.
 *
 * @author katherine
 * @see IMeshElement
 */
public class IMeshElementRichletTest extends WebDriverTestCase {
	@Test
	public void paging() {
		connect("/base_components/IMeshElement/paging");
		assertEquals(3, jq(".z-treecell-content").length());
		assertEquals(3, jq(".z-row-content").length());
		assertEquals(3, jq(".z-listcell-content").length());
	}

	@Test
	public void nativeScrollbar() {
		connect("/base_components/IMeshElement/nativeScrollbar");
		assertEquals(3, jq(".z-scrollbar-vertical-embed").length());
	}

	@Test
	public void sizedByContent() {
		connect("/base_components/IMeshElement/sizedByContent");
		assertTrue(jq(".z-treecell-content").width() < jq(".z-treerow").width());
		assertTrue(jq(".z-row-inner").attr("style").contains("width"));
		assertTrue(jq(".z-listcell").attr("style").contains("width"));
	}

	@Test
	public void span() {
		connect("/base_components/IMeshElement/span");
		assertEquals(jq(".z-treecol").width(), jq(".z-treecol").width(), 2);
		assertEquals(jq(".z-column").width(), jq(".z-column").width(), 2);
		assertEquals(jq(".z-listheader").width(), jq(".z-listheader").width(), 2);
	}
}