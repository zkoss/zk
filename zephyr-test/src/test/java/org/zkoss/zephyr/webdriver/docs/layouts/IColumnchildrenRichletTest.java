/* IColumnchildrenRichletTest.java

	Purpose:

	Description:

	History:
		Mon Apr 18 12:23:16 CST 2022, Created by katherine

Copyright (C) 2022 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zephyr.webdriver.docs.layouts;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;

/**
 * A set of unit test for {@link org.zkoss.zephyrex.zpr.IColumnchildren} Java Docs.
 * And also refers to something else on <a href="https://www.zkoss.org/wiki/ZK_Component_Reference/Layouts/ColumnChildren">ColumnChildren</a>,
 * if any.
 *
 * @author katherine
 * @see org.zkoss.zephyrex.zpr.IColumnchildren
 */
public class IColumnchildrenRichletTest extends WebDriverTestCase {
	@Test
	public void columnlayout() {
		connect("/layouts/iColumnChildren/columnlayout");
		assertEquals(2, jq(".z-columnlayout").children().length());
		assertEquals("child", jq(".z-columnchildren:eq(0)").text());
		assertEquals(jq(".z-columnlayout").outerWidth() * 0.2,
				jq(".z-columnchildren").outerWidth(), 1);
	}
}