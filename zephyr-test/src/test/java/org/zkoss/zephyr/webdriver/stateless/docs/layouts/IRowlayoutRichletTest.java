/* IRowlayoutRichletTest.java

	Purpose:

	Description:

	History:
		Mon Apr 18 14:28:41 CST 2022, Created by katherine

Copyright (C) 2022 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zephyr.webdriver.stateless.docs.layouts;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;

/**
 * A set of unit test for {@link org.zkoss.statelessex.sul.IRowlayout} Java Docs.
 * And also refers to something else on <a href="https://www.zkoss.org/wiki/ZK_Component_Reference/Layouts/Rowlayout">Rowlayout</a>,
 * if any.
 *
 * @author katherine
 * @see org.zkoss.statelessex.sul.IRowlayout
 */
public class IRowlayoutRichletTest extends WebDriverTestCase {
	@Test
	public void hflex() {
		connect("/layouts/iRowlayout/hflex");
		click(jq("@button"));
		waitResponse();
		assertTrue(hasError());
		assertTrue(jq(".z-label:contains(readonly)").exists());
	}

	@Test
	public void ncols() {
		connect("/layouts/iRowlayout/ncols");
		int w = jq(".z-rowchildren").outerWidth();
		click(jq("@button"));
		waitResponse();
		assertEquals(w * 2, jq(".z-rowchildren").outerWidth(), 2);
	}

	@Test
	public void spacing() {
		connect("/layouts/iRowlayout/spacing");
		int offsetLeft = jq(".z-rowchildren:eq(1)").offsetLeft();
		click(jq("@button"));
		waitResponse();
		assertTrue(offsetLeft < jq(".z-rowchildren:eq(1)").offsetLeft());
	}

	@Test
	public void width() {
		connect("/layouts/iRowlayout/width");
		click(jq("@button"));
		waitResponse();
		assertTrue(hasError());
		assertTrue(jq(".z-label:contains(readonly)").exists());
	}
}