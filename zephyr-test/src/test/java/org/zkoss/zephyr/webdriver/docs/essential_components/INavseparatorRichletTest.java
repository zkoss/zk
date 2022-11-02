/* INavseparatorRichletTest.java

	Purpose:

	Description:

	History:
		Wed Apr 06 16:04:54 CST 2022, Created by katherine

Copyright (C) 2022 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zephyr.webdriver.docs.essential_components;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;

/**
 * A set of unit test for {@link org.zkoss.statelessex.sul.INavseparator} Java Docs.
 * And also refers to something else on <a href="https://www.zkoss.org/wiki/ZK_Component_Reference/Essential_Components/Navseparator">Navseparator</a>,
 * if any.
 *
 * @author katherine
 * @see org.zkoss.statelessex.sul.INavseparator
 */
public class INavseparatorRichletTest extends WebDriverTestCase {
	@Test
	public void navseparator() {
		connect("/essential_components/iNavseparator");
		assertTrue(jq(".z-navseparator").exists());
		assertEquals(3, jq(".z-navbar").children().children().length());
	}
}