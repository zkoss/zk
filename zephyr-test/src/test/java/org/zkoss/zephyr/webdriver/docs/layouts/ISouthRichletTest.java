/* ISouthRichletTest.java

	Purpose:

	Description:

	History:
		Mon Apr 11 15:57:18 CST 2022, Created by katherine

Copyright (C) 2022 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zephyr.webdriver.docs.layouts;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;

/**
 * A set of unit test for {@link org.zkoss.zephyr.zpr.ISouth} Java Docs.
 * And also refers to something else on <a href="https://www.zkoss.org/wiki/ZK_Component_Reference/Layouts/Borderlayout/South">South</a>,
 * if any.
 *
 * @author katherine
 * @see org.zkoss.zephyr.zpr.ISouth
 */
public class ISouthRichletTest extends WebDriverTestCase {
	@Test
	public void cmargin() {
		connect("/data/iSouth/cmargin");
		click(jq(".z-icon-angle-double-down"));
		int height = jq(".z-center").outerHeight();
		click(jq("@button"));
		waitResponse();
		assertTrue(height > jq(".z-center").outerHeight());
	}

	@Test
	public void size() {
		connect("/data/iSouth/size");
		assertTrue(jq(".z-south").attr("style").contains("height: 100px"));
		click(jq("@button"));
		waitResponse();
		assertTrue(jq(".z-south").attr("style").contains("height: 200px"));
	}
}