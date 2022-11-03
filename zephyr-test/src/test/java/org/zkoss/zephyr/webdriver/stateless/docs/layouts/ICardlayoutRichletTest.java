/* ICardlayoutRichletTest.java

	Purpose:

	Description:

	History:
		Mon Apr 18 12:24:01 CST 2022, Created by katherine

Copyright (C) 2022 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zephyr.webdriver.stateless.docs.layouts;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;

/**
 * A set of unit test for {@link org.zkoss.statelessex.sul.ICardlayout} Java Docs.
 * And also refers to something else on <a href="https://www.zkoss.org/wiki/ZK_Component_Reference/Layouts/Cardlayout">Cardlayout</a>,
 * if any.
 *
 * @author katherine
 * @see org.zkoss.statelessex.sul.ICardlayout
 */
public class ICardlayoutRichletTest extends WebDriverTestCase {
	@Test
	public void orient() {
		connect("/Layouts/iCardlayout/orient");
		String script = "zk.$('$cardlayout')._orient";
		assertEquals("vertical", WebDriverTestCase.getEval(script));
		click(jq("@button"));
		waitResponse();
		assertEquals("horizontal", WebDriverTestCase.getEval(script));
	}
}