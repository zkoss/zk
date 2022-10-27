/* ILinelayoutRichletTest.java

	Purpose:

	Description:

	History:
		Thu Apr 07 12:29:03 CST 2022, Created by katherine

Copyright (C) 2022 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.stateless.webdriver.docs.layouts;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;

/**
 * A set of unit test for {@link org.zkoss.statelessex.sul.ILinelayout} Java Docs.
 * And also refers to something else on <a href="https://www.zkoss.org/wiki/ZK_Component_Reference/Layouts/Linelayout">Linelayout</a>,
 * if any.
 *
 * @author katherine
 * @see org.zkoss.statelessex.sul.ILinelayout
 */
public class ILinelayoutRichletTest extends WebDriverTestCase {
	@Test
	public void firstScale() {
		connect("/layouts/iLinelayout/firstScale");
		assertTrue(jq(".z-linelayout-first").attr("style").contains("flex-grow: 2"));
		click(jq("@button"));
		waitResponse();
		assertTrue(jq(".z-linelayout-first").attr("style").contains("flex-grow: 3"));
	}

	@Test
	public void lastScale() {
		connect("/layouts/iLinelayout/lastScale");
		assertTrue(jq(".z-linelayout-last").attr("style").contains("flex-grow: 2"));
		click(jq("@button"));
		waitResponse();
		assertTrue(jq(".z-linelayout-last").attr("style").contains("flex-grow: 3"));
	}

	@Test
	public void lineStyle() {
		connect("/layouts/iLinelayout/lineStyle");
		assertTrue(jq(".z-linelayout-line").attr("style").contains("red"));
		click(jq("@button"));
		waitResponse();
		assertTrue(jq(".z-linelayout-line").attr("style").contains("blue"));
	}

	@Test
	public void orient() {
		connect("/layouts/iLinelayout/orient");
		assertTrue(jq(".z-linelayout").hasClass("z-linelayout-horizontal"));
		click(jq("@button"));
		waitResponse();
		assertTrue(jq(".z-linelayout").hasClass("z-linelayout-vertical"));
	}
}