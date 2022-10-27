/* IWestRichletTest.java

	Purpose:

	Description:

	History:
		Mon Apr 11 15:59:42 CST 2022, Created by katherine

Copyright (C) 2022 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zephyr.webdriver.docs.layouts;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import org.zkoss.stateless.zpr.IWest;
import org.zkoss.test.webdriver.WebDriverTestCase;

/**
 * A set of unit test for {@link IWest} Java Docs.
 * And also refers to something else on <a href="https://www.zkoss.org/wiki/ZK_Component_Reference/Layouts/Borderlayout/West">West</a>,
 * if any.
 *
 * @author katherine
 * @see IWest
 */
public class IWestRichletTest extends WebDriverTestCase {
	@Test
	public void cmargin() {
		connect("/data/iWest/cmargin");
		click(jq(".z-icon-caret-left"));
		int height = jq(".z-west-collapsed").outerHeight();
		click(jq("@button"));
		waitResponse();
		assertTrue(height > jq(".z-west-collapsed").outerHeight());
	}

	@Test
	public void size() {
		connect("/data/iWest/size");
		assertTrue(jq(".z-west").attr("style").contains("width: 100px"));
		click(jq("@button"));
		waitResponse();
		assertTrue(jq(".z-west").attr("style").contains("width: 200px"));
	}
}