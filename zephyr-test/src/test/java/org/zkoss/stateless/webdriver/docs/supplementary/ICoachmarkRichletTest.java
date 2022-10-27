/* ICoachmarkRichletTest.java

	Purpose:

	Description:

	History:
		Wed Apr 20 16:17:19 CST 2022, Created by katherine

Copyright (C) 2022 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.stateless.webdriver.docs.supplementary;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;

/**
 * A set of unit test for {@link org.zkoss.statelessex.sul.ICoachmark} Java Docs.
 * And also refers to something else on <a href="https://www.zkoss.org/wiki/ZK_Component_Reference/Supplementary/Coachmark">Coachmark</a>,
 * if any.
 *
 * @author katherine
 * @see org.zkoss.statelessex.sul.ICoachmark
 */
public class ICoachmarkRichletTest extends WebDriverTestCase {
	@Test
	public void position() {
		connect("/supplementary/iCoachmark/position");
		int offsetLeft = jq(".z-coachmark").offsetLeft();
		click(jq("@button"));
		waitResponse();
		assertTrue(offsetLeft < jq(".z-coachmark").offsetLeft());
	}

	@Test
	public void target() {
		connect("/supplementary/iCoachmark/target");
		int offsetLeft = jq(".z-coachmark").offsetLeft();
		click(jq(".z-coachmark-close"));
		click(jq("@button:eq(0)"));
		waitResponse();
		assertTrue(offsetLeft > jq(".z-coachmark").offsetLeft());
	}
}