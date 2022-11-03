/* INavbarRichletTest.java

	Purpose:

	Description:

	History:
		Wed Apr 06 16:04:27 CST 2022, Created by katherine

Copyright (C) 2022 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zephyr.webdriver.stateless.docs.essential_components;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;

/**
 * A set of unit test for {@link org.zkoss.statelessex.sul.INavbar} Java Docs.
 * And also refers to something else on <a href="https://www.zkoss.org/wiki/ZK_Component_Reference/Essential_Components/Navbar">Navbar</a>,
 * if any.
 *
 * @author katherine
 * @see org.zkoss.statelessex.sul.INavbar
 */
public class INavbarRichletTest extends WebDriverTestCase {
	@Test
	public void autoclose() {
		connect("/essential_components/iNavbar/autoclose");
		click(jq(".z-nav:contains(nav 1)"));
		assertTrue(jq(".z-navitem:contains(a)").isVisible());
		waitResponse();
		click(jq(".z-navitem:contains(nav 2)"));
		waitResponse();
		assertEquals(0, jq(".z-navitem:contains(a)").offsetTop());
		click(jq("@button"));
		waitResponse();
		click(jq(".z-nav:contains(nav 1)"));
		click(jq(".z-navitem:contains(nav 2)"));
		waitResponse();
		assertNotEquals(0, jq(".z-navitem:contains(a)").offsetTop());
	}

	@Test
	public void orient() {
		connect("/essential_components/iNavbar/orient");
		assertTrue(jq(".z-navbar").hasClass("z-navbar-vertical"));
		click(jq("@button"));
		waitResponse();
		assertTrue(jq(".z-navbar").hasClass("z-navbar-horizontal"));
	}

	@Test
	public void collapsed() {
		connect("/essential_components/iNavbar/collapsed");
		assertFalse(jq(".z-nav-text").isVisible());
		click(jq("@button"));
		waitResponse();
		assertTrue(jq(".z-nav-text").isVisible());
	}
}