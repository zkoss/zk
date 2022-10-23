/* ILayoutRichletTest.java

	Purpose:

	Description:

	History:
		Wed Mar 30 17:06:45 CST 2022, Created by katherine

Copyright (C) 2022 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zephyr.webdriver.docs.layouts;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;

/**
 * A set of unit test for {@link org.zkoss.zephyr.zpr.ILayout} Java Docs.
 *
 * @author katherine
 * @see org.zkoss.zephyr.zpr.ILayout
 */
public class ILayoutRichletTest extends WebDriverTestCase {
	@Test
	public void test1() {
		connect("/layouts/iLayout/spacing");
		assertTrue(jq(".z-vlayout-inner").attr("style").contains("padding-bottom:5px"));
		assertTrue(jq(".z-hlayout-inner").attr("style").contains("padding-right: 5px"));
		click(jq("@button"));
		waitResponse();
		assertTrue(jq(".z-vlayout-inner").attr("style").contains("padding-bottom: 10px"));
		assertTrue(jq(".z-hlayout-inner").attr("style").contains("padding-right: 10px"));
	}
}