/* INorthRichletTest.java

	Purpose:

	Description:

	History:
		Mon Apr 11 15:56:28 CST 2022, Created by katherine

Copyright (C) 2022 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zephyr.webdriver.docs.layouts;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import org.zkoss.stateless.zpr.INorth;
import org.zkoss.test.webdriver.WebDriverTestCase;

/**
 * A set of unit test for {@link INorth} Java Docs.
 * And also refers to something else on <a href="https://www.zkoss.org/wiki/ZK_Component_Reference/Layouts/Borderlayout/North">North</a>,
 * if any.
 *
 * @author katherine
 * @see INorth
 */
public class INorthRichletTest extends WebDriverTestCase {
	@Test
	public void cmargin() {
		connect("/data/iNorth/cmargin");
		click(jq(".z-icon-caret-up"));
		waitResponse();
		assertEquals(10, jq(".z-north-collapsed").offsetTop());
		click(jq("@button"));
		waitResponse();
		assertEquals(20, jq(".z-north-collapsed").offsetTop());
	}

	@Test
	public void size() {
		connect("/data/iNorth/size");
		assertTrue(jq(".z-north").attr("style").contains("height: 100px"));
		click(jq("@button"));
		waitResponse();
		assertTrue(jq(".z-north").attr("style").contains("height: 200px"));
	}
}