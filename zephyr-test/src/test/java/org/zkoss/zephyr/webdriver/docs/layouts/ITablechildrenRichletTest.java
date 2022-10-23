/* ITablechildrenRichletTest.java

	Purpose:

	Description:

	History:
		Tue Apr 19 14:28:49 CST 2022, Created by katherine

Copyright (C) 2022 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zephyr.webdriver.docs.layouts;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;

/**
 * A set of unit test for {@link org.zkoss.zephyrex.zpr.ITablechildren} Java Docs.
 * And also refers to something else on <a href="https://www.zkoss.org/wiki/ZK_Component_Reference/Layouts/Tablelayout/Tablechildren">Tablechildren</a>,
 * if any.
 *
 * @author katherine
 * @see org.zkoss.zephyrex.zpr.ITablechildren
 */
public class ITablechildrenRichletTest extends WebDriverTestCase {
	@Test
	public void colspan() {
		connect("/layouts/iTablechildren/colspan");
		assertEquals("3", jq(".z-tablechildren:eq(0)").attr("colspan"));
		click(jq("@button"));
		waitResponse();
		assertEquals("2", jq(".z-tablechildren:eq(0)").attr("colspan"));
	}

	@Test
	public void rowspan() {
		connect("/layouts/iTablechildren/rowspan");
		assertEquals("2", jq(".z-tablechildren:eq(0)").attr("rowspan"));
		click(jq("@button"));
		waitResponse();
		assertEquals("null", jq(".z-tablechildren:eq(0)").attr("rowspan"));
	}
}