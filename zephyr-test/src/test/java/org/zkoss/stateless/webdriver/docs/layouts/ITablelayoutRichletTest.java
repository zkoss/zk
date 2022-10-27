/* ITablelayoutRichletTest.java

	Purpose:

	Description:

	History:
		Tue Apr 19 14:25:55 CST 2022, Created by katherine

Copyright (C) 2022 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.stateless.webdriver.docs.layouts;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;

/**
 * A set of unit test for {@link org.zkoss.statelessex.sul.ITablelayout} Java Docs.
 * And also refers to something else on <a href="https://www.zkoss.org/wiki/ZK_Component_Reference/Layouts/Tablelayout">Tablelayout</a>,
 * if any.
 *
 * @author katherine
 * @see org.zkoss.statelessex.sul.ITablelayout
 */
public class ITablelayoutRichletTest extends WebDriverTestCase {
	@Test
	public void columns() {
		connect("/layouts/iTablelayout/columns");
		assertEquals(2, jq(".z-tablelayout tr:eq(0)").children().length());
		click(jq("@button"));
		waitResponse();
		assertEquals(1, jq(".z-tablelayout tr:eq(0)").children().length());
	}
}