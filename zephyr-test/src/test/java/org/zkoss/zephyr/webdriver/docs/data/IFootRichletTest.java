/* IFootRichletTest.java

	Purpose:

	Description:

	History:
		Fri Apr 08 14:14:35 CST 2022, Created by katherine

Copyright (C) 2022 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zephyr.webdriver.docs.data;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;

/**
 * A set of unit test for {@link org.zkoss.zephyr.zpr.IFoot} Java Docs.
 * And also refers to something else on <a href="https://www.zkoss.org/wiki/ZK_Component_Reference/Data/Listbox/Foot">Foot</a>,
 * if any.
 *
 * @author katherine
 * @see org.zkoss.zephyr.zpr.IFoot
 */
public class IFootRichletTest extends WebDriverTestCase {
	@Test
	public void widthAndHflex() {
		connect("/data/iGrid/iFoot/widthAndHflex");
		click(jq("@button:eq(0)"));
		waitResponse();
		assertTrue(hasError());
		assertTrue(jq(".z-label:contains(readonly)").exists());
		click(jq(".z-messagebox-buttons button"));
		waitResponse();
		click(jq("@button:eq(1)"));
		waitResponse();
		assertTrue(hasError());
		assertTrue(jq(".z-label:contains(readonly)").exists());
	}
}