/* INavitemRichletTest.java

	Purpose:

	Description:

	History:
		Wed Apr 06 16:04:38 CST 2022, Created by katherine

Copyright (C) 2022 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zephyr.webdriver.docs.essential_components;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;

/**
 * A set of unit test for {@link org.zkoss.statelessex.zpr.INavitem} Java Docs.
 * And also refers to something else on <a href="https://www.zkoss.org/wiki/ZK_Component_Reference/Essential_Components/Navitem">Navitem</a>,
 * if any.
 *
 * @author katherine
 * @see org.zkoss.statelessex.zpr.INavitem
 */
public class INavitemRichletTest extends WebDriverTestCase {
	@Test
	public void badgeText() {
		connect("/essential_components/iNavitem/badgeText");
		assertEquals("1", jq(".z-navitem-info").text());
		click(jq("@button"));
		waitResponse();
		assertEquals("2", jq(".z-navitem-info").text());
	}

	@Test
	public void content() {
		connect("/essential_components/iNavitem/content");
		assertEquals("test", jq(".z-navitem-content:eq(1)").text());
		click(jq("@button"));
		waitResponse();
		assertEquals("test2", jq(".z-navitem-content:eq(1)").text());
	}

	@Test
	public void href() {
		connect("/essential_components/iNavitem/href");
		assertEquals("www.google.com", jq(".z-navitem-content:eq(1)").attr("href"));
		click(jq("@button"));
		waitResponse();
		assertEquals("www.yahoo.com", jq(".z-navitem-content:eq(1)").attr("href"));
	}

	@Test
	public void target() {
		connect("/essential_components/iNavitem/target");
		assertEquals("www.google.com", jq(".z-navitem-content:eq(1)").attr("target"));
		click(jq("@button"));
		waitResponse();
		assertEquals("target2", jq(".z-navitem-content:eq(1)").attr("target"));
	}
}