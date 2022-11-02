/* IDivRichletTest.java

	Purpose:

	Description:

	History:
		Thu Mar 10 16:03:28 CST 2022, Created by katherine

Copyright (C) 2022 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zephyr.webdriver.docs.containers;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import org.zkoss.stateless.sul.IDiv;
import org.zkoss.test.webdriver.WebDriverTestCase;

/**
 * A set of unit test for {@link IDiv} Java Docs.
 * And also refers to something else on <a href="https://www.zkoss.org/wiki/ZK_Component_Reference/Containers/Div">Div</a>,
 * if any.
 *
 * @author katherine
 * @see IDiv
 */
public class IDivRichletTest extends WebDriverTestCase {
	@Test
	public void size() {
		connect("/containers/iDiv/size");
		assertTrue(jq(".z-div").attr("style").contains("10px"));
		click(jq("@button"));
		waitResponse();
		assertTrue(jq(".z-div").attr("style").contains("100px"));
	}

	@Test
	public void children() {
		connect("/containers/iDiv/children");
		assertTrue(jq(".z-div:eq(0) .z-div:eq(0) .z-div:eq(0)").exists());
		click(jq("@button"));
		waitResponse();
		assertTrue(jq(".z-label").exists());
	}
}