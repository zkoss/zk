/* IMultisliderRichletTest.java

	Purpose:

	Description:

	History:
		Mon Mar 07 15:50:43 CST 2022, Created by katherine

Copyright (C) 2022 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.stateless.webdriver.docs.input;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;

/**
 * A set of unit test for {@link org.zkoss.statelessex.sul.IMultislider} Java Docs.
 * And also refers to something else on <a href="https://www.zkoss.org/wiki/ZK_Component_Reference/Input/Multislider">Multislider</a>,
 * if any.
 *
 * @author katherine
 * @see org.zkoss.statelessex.sul.IMultislider
 */
public class IMultisliderRichletTest extends WebDriverTestCase {
	@Test
	public void orient() {
		connect("/input/iMultislider/orient");
		assertTrue(jq(".z-multislider-vertical").exists());
		click(jq("@button"));
		waitResponse();
		assertTrue(jq(".z-multislider-horizontal").exists());
	}

	@Test
	public void minMax() {
		connect("/input/iMultislider/minMax");
		assertEquals("10", jq(".z-multislider-mark-label:first").text());
		assertEquals("90", jq(".z-multislider-mark-label:last").text());
		click(jq("@button"));
		waitResponse();
		assertEquals("20", jq(".z-multislider-mark-label:first").text());
		assertEquals("80", jq(".z-multislider-mark-label:last").text());
	}
}