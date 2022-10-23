/* IDoubleboxRichletTest.java

	Purpose:

	Description:

	History:
		Wed Mar 02 15:49:35 CST 2022, Created by katherine

Copyright (C) 2022 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zephyr.webdriver.docs.input;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;

/**
 * A set of unit test for {@link org.zkoss.zephyr.zpr.IDoublebox} Java Docs.
 * And also refers to something else on <a href="https://www.zkoss.org/wiki/ZK_Component_Reference/Input/Doublebox">Doublebox</a>,
 * if any.
 *
 * @author katherine
 * @see org.zkoss.zephyr.zpr.IDoublebox
 */
public class IDoubleboxRichletTest extends WebDriverTestCase {
	@Test
	public void value() {
		connect("/input/iDoublebox/value");
		assertEquals("1.2", jq(".z-doublebox").val());
		click(jq("@button"));
		waitResponse();
		assertEquals("1", jq(".z-doublebox").val());
	}
}