/* IMenubarRichletTest.java

	Purpose:

	Description:

	History:
		Tue Apr 12 11:42:39 CST 2022, Created by katherine

Copyright (C) 2022 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.stateless.webdriver.docs.essential_components;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import org.zkoss.stateless.sul.IMenubar;
import org.zkoss.test.webdriver.WebDriverTestCase;

/**
 * A set of unit test for {@link IMenubar} Java Docs.
 * And also refers to something else on <a href="https://www.zkoss.org/wiki/ZK_Component_Reference/Essential_Components/Menu/Menubar">Menubar</a>,
 * if any.
 *
 * @author katherine
 * @see IMenubar
 */
public class IMenubarRichletTest extends WebDriverTestCase {
	@Test
	public void autodrop() {
		connect("/essential_components/iMenubar/autodrop");
		getActions().moveToElement(toElement(jq(".z-menu"))).build().perform();
		assertTrue(jq(".z-menupopup").isVisible());
		click(jq("@button"));
		waitResponse();
		assertFalse(jq(".z-menupopup").isVisible());
	}

	@Test
	public void orient() {
		connect("/essential_components/iMenubar/orient");
		assertTrue(jq(".z-menubar-horizontal").exists());
		click(jq("@button"));
		waitResponse();
		assertTrue(jq(".z-menubar-vertical").exists());
	}

	@Test
	public void scrollable() {
		connect("/essential_components/iMenubar/scrollable");
		assertFalse(jq(".z-menubar-scrollable").exists());
		click(jq("@button"));
		waitResponse();
		assertTrue(jq(".z-menubar-scrollable").exists());
	}
}