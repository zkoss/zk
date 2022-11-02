/* IMenuseparatorRichletTest.java

	Purpose:

	Description:

	History:
		Tue Apr 12 11:42:52 CST 2022, Created by katherine

Copyright (C) 2022 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zephyr.webdriver.docs.essential_components;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import org.zkoss.stateless.sul.IMenuseparator;
import org.zkoss.test.webdriver.WebDriverTestCase;

/**
 * A set of unit test for {@link IMenuseparator} Java Docs.
 * And also refers to something else on <a href="https://www.zkoss.org/wiki/ZK_Component_Reference/Essential_Components/Menu/Menuseparator">Menuseparator</a>,
 * if any.
 *
 * @author katherine
 * @see IMenuseparator
 */
public class IMenuseparatorRichletTest extends WebDriverTestCase {
	@Test
	public void menuseparator() {
		connect("/essential_components/iMenuseparator/menuseparator");
		click(jq(".z-menu-icon"));
		waitResponse();
		assertTrue(jq(".z-menuitem").next().hasClass("z-menuseparator"));
		assertTrue(jq(".z-menuseparator").attr("style").contains("width:10px"));
	}
}