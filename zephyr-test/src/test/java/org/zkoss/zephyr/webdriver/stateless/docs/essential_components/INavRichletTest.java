/* INavRichletTest.java

	Purpose:

	Description:

	History:
		Wed Apr 06 16:04:16 CST 2022, Created by katherine

Copyright (C) 2022 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zephyr.webdriver.stateless.docs.essential_components;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;

/**
 * A set of unit test for {@link org.zkoss.statelessex.sul.INav} Java Docs.
 * And also refers to something else on <a href="https://www.zkoss.org/wiki/ZK_Component_Reference/Essential_Components/Nav">Nav</a>,
 * if any.
 *
 * @author katherine
 * @see org.zkoss.statelessex.sul.INav
 */
public class INavRichletTest extends WebDriverTestCase {
	@Test
	public void badgeText() {
		connect("/essential_components/iNav/badgeText");
		assertEquals("1", jq(".z-nav-info").text());
		click(jq("@button"));
		waitResponse();
		assertEquals("2", jq(".z-nav-info").text());
	}

	@Test
	public void open() {
		connect("/essential_components/iNav/open");
		assertTrue(jq(".z-navitem").isVisible());
		click(jq("@button"));
		waitResponse();
		assertFalse(jq(".z-navitem").isVisible());
	}
}