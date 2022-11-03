/* IAbsolutelayoutRichletTest.java

	Purpose:

	Description:

	History:
		Thu Feb 17 16:58:37 CST 2022, Created by katherine

Copyright (C) 2022 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zephyr.webdriver.stateless.docs.layouts;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import org.zkoss.stateless.sul.IAbsolutelayout;
import org.zkoss.test.webdriver.WebDriverTestCase;

/**
 * A set of unit test for {@link IAbsolutelayout} Java Docs.
 * And also refers to something else on <a href="https://www.zkoss.org/wiki/ZK_Component_Reference/Layouts/Absolutelayout">Absolutelayout</a>,
 * if any.
 *
 * @author katherine
 * @see IAbsolutelayout
 */
public class IAbsolutelayoutRichletTest extends WebDriverTestCase {
	@Test
	public void example() {
		connect("/layouts/iabsolutelayout/example");
		int parentOffsetTop = jq(".z-absolutelayout").offsetTop();
		int parentOffsetLeft = jq(".z-absolutelayout").offsetLeft();
		System.out.println(parentOffsetLeft);
		assertEquals(100, jq(".z-absolutechildren:eq(0)").offsetTop() - parentOffsetTop);
		assertEquals(60, jq(".z-absolutechildren:eq(0)").offsetLeft() - parentOffsetLeft);
		assertEquals(200, jq(".z-absolutechildren:eq(1)").offsetTop() - parentOffsetTop);
		assertEquals(160, jq(".z-absolutechildren:eq(1)").offsetLeft() - parentOffsetLeft);
		assertEquals(300, jq(".z-absolutechildren:eq(2)").offsetTop() - parentOffsetTop);
		assertEquals(260, jq(".z-absolutechildren:eq(2)").offsetLeft() - parentOffsetLeft);
	}
}