/* IRowchildrenRichletTest.java

	Purpose:

	Description:

	History:
		Mon Apr 18 14:29:09 CST 2022, Created by katherine

Copyright (C) 2022 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zephyr.webdriver.stateless.docs.layouts;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;

/**
 * A set of unit test for {@link org.zkoss.statelessex.sul.IRowchildren} Java Docs.
 * And also refers to something else on <a href="https://www.zkoss.org/wiki/ZK_Component_Reference/Layouts/Rowlayout/Rowchildren">Rowchildren</a>,
 * if any.
 *
 * @author katherine
 * @see org.zkoss.statelessex.sul.IRowchildren
 */
public class IRowchildrenRichletTest extends WebDriverTestCase {
	@Test
	public void colspan() {
		connect("/layouts/iRowchildren/colspan");
		assertTrue(jq(".z-rowchildren").hasClass("colspan3"));
		click(jq("@button"));
		waitResponse();
		assertTrue(jq(".z-rowchildren").hasClass("colspan2"));
	}

	@Test
	public void offset() {
		connect("/layouts/iRowchildren/offset");
		assertTrue(jq(".z-rowchildren").hasClass("offset3"));
		click(jq("@button"));
		waitResponse();
		assertTrue(jq(".z-rowchildren").hasClass("offset2"));
	}
}