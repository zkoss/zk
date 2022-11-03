/* IEastRichletTest.java

	Purpose:

	Description:

	History:
		Mon Apr 11 15:58:02 CST 2022, Created by katherine

Copyright (C) 2022 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zephyr.webdriver.stateless.docs.layouts;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import org.zkoss.stateless.sul.IEast;
import org.zkoss.test.webdriver.WebDriverTestCase;

/**
 * A set of unit test for {@link IEast} Java Docs.
 * And also refers to something else on <a href="https://www.zkoss.org/wiki/ZK_Component_Reference/Layouts/Borderlayout/East">East</a>,
 * if any.
 *
 * @author katherine
 * @see IEast
 */
public class IEastRichletTest extends WebDriverTestCase {
	@Test
	public void cmargin() {
		connect("/data/iEast/cmargin");
		click(jq(".z-icon-caret-right"));
		int height = jq(".z-east-collapsed").outerHeight();
		click(jq("@button"));
		waitResponse();
		assertTrue(height > jq(".z-east-collapsed").outerHeight());
	}

	@Test
	public void size() {
		connect("/data/iEast/size");
		assertTrue(jq(".z-east").attr("style").contains("width: 100px"));
		click(jq("@button"));
		waitResponse();
		assertTrue(jq(".z-east").attr("style").contains("width: 200px"));
	}
}