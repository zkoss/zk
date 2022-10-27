/* IBorderlayoutRichletTest.java

	Purpose:

	Description:

	History:
		Tue Apr 12 10:34:22 CST 2022, Created by katherine

Copyright (C) 2022 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.stateless.webdriver.docs.layouts;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import org.zkoss.stateless.sul.IBorderlayout;
import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

/**
 * A set of unit test for {@link IBorderlayout} Java Docs.
 * And also refers to something else on <a href="https://www.zkoss.org/wiki/ZK_Component_Reference/Layouts/Borderlayout">Borderlayout</a>,
 * if any.
 *
 * @author katherine
 * @see IBorderlayout
 */
public class IBorderlayoutRichletTest extends WebDriverTestCase {
	@Test
	public void children() {
		connect("/layouts/iBorderlayout/children");
		int width = 800;
		JQuery splitter = jq(".z-west-splitter");
		JQuery west = jq(".z-west");
		JQuery east = jq(".z-east");

		assertEquals(800, jq(".z-borderlayout").width());
		assertEquals(jq(".z-borderlayout").height() * 0.1, jq(".z-north").outerHeight(), 1);
		assertEquals(width * 0.25, west.outerWidth());
		assertEquals(width * 0.5 - splitter.width(), jq(".z-center").outerWidth(), 1);
		assertEquals(width * 0.25, east.outerWidth(), 1);
		assertEquals(100, jq(".z-south").outerHeight());
		dragdropTo(splitter, 0, 0, 500, 0);
		assertEquals(west.outerWidth(), 300);
		click(jq(".z-icon-angle-double-left"));
		waitResponse();
		assertEquals(jq(".z-center").outerWidth(),
				width - jq(".z-west-collapsed").outerWidth() - east.outerWidth());
	}
}