/* ISpanRichletTest.java

	Purpose:

	Description:

	History:
		Wed Apr 20 16:09:51 CST 2022, Created by katherine

Copyright (C) 2022 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zephyr.webdriver.docs.containers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;

/**
 * A set of unit test for {@link org.zkoss.zephyr.zpr.ISpan} Java Docs.
 * And also refers to something else on <a href="https://www.zkoss.org/wiki/ZK_Component_Reference/Containers/Span">Span</a>,
 * if any.
 *
 * @author katherine
 * @see org.zkoss.zephyr.zpr.ISpan
 */
public class ISpanRichletTest extends WebDriverTestCase {
	@Test
	public void span() {
		connect("/containers/iSpan/span");
		assertTrue(jq(".z-span").children().hasClass("z-span"));
		click(jq("@button"));
		waitResponse();
		assertEquals("children", jq(".z-span:eq(1)").children().text());
	}
}