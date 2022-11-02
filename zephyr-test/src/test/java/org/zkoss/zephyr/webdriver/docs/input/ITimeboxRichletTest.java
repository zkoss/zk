/* ITimeboxRichletTest.java

	Purpose:

	Description:

	History:
		Fri Mar 04 09:33:08 CST 2022, Created by katherine

Copyright (C) 2022 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zephyr.webdriver.docs.input;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import org.zkoss.stateless.sul.ITimebox;
import org.zkoss.test.webdriver.WebDriverTestCase;

/**
 * A set of unit test for {@link ITimebox} Java Docs.
 * And also refers to something else on <a href="https://www.zkoss.org/wiki/ZK_Component_Reference/Input/Timebox">Timebox</a>,
 * if any.
 *
 * @author katherine
 * @see ITimebox
 */
public class ITimeboxRichletTest extends WebDriverTestCase {
	@Test
	public void test1() {
		connect("/input/iTimebox/buttonVisible");
		assertTrue(jq(".z-timebox-disabled").exists());
		click(jq("@button"));
		waitResponse();
		assertFalse(jq(".z-timebox-disabled").exists());
	}
}