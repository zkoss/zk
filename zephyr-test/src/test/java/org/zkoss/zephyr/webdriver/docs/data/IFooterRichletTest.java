/* IFooterRichletTest.java

	Purpose:

	Description:

	History:
		Fri Apr 08 18:04:14 CST 2022, Created by katherine

Copyright (C) 2022 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zephyr.webdriver.docs.data;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;

/**
 * A set of unit test for {@link org.zkoss.zephyr.zpr.IFooter} Java Docs.
 * And also refers to something else on <a href="https://www.zkoss.org/wiki/ZK_Component_Reference/Data/Listbox/Footer">Footer</a>,
 * if any.
 *
 * @author katherine
 * @see org.zkoss.zephyr.zpr.IFooter
 */
public class IFooterRichletTest extends WebDriverTestCase {

	@Test
	public void image() {
		connect("/data/iGrid/iFooter/image");
		assertTrue(jq(".z-footer img").attr("src").contains("ZK-Logo.gif"));
		click(jq("@button"));
		waitResponse();
		assertTrue(jq(".z-footer img").attr("src").contains("ZK-Logo-old.gif"));
	}
}