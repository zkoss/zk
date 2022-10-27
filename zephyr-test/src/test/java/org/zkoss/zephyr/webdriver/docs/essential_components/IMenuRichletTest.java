/* IMenuRichletTest.java

	Purpose:

	Description:

	History:
		Tue Apr 12 11:41:19 CST 2022, Created by katherine

Copyright (C) 2022 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zephyr.webdriver.docs.essential_components;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import org.zkoss.stateless.zpr.IMenu;
import org.zkoss.test.webdriver.WebDriverTestCase;

/**
 * A set of unit test for {@link IMenu} Java Docs.
 * And also refers to something else on <a href="https://www.zkoss.org/wiki/ZK_Component_Reference/Essential_Components/Menu">Menu</a>,
 * if any.
 *
 * @author katherine
 * @see IMenu
 */
public class IMenuRichletTest extends WebDriverTestCase {
	@Test
	public void content() {
		connect("/essential_components/iMenu/content");
		click(jq(".z-menu"));
		click(jq(".z-icon-caret-right"));
		assertTrue(jq(".z-menu-image:eq(1)").attr("src").contains("ZK-Logo.gif"));
		assertTrue(jq(".z-colorpalette-newcolor").attr("style").contains("rgb(24, 77, 198)"));
		click(jq("@button"));
		waitResponse();
		click(jq(".z-menu"));
		click(jq(".z-icon-caret-right"));
		assertTrue(jq(".z-colorpalette-newcolor").attr("style").contains("rgb(255, 255, 255)"));
	}
}