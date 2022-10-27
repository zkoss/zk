/* IPopupRichletTest.java

	Purpose:

	Description:

	History:
		Mon Apr 11 14:33:02 CST 2022, Created by katherine

Copyright (C) 2022 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zephyr.webdriver.docs.essential_components;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import org.zkoss.stateless.zpr.IPopup;
import org.zkoss.test.webdriver.WebDriverTestCase;

/**
 * A set of unit test for {@link IPopup} Java Docs.
 * And also refers to something else on <a href="https://www.zkoss.org/wiki/ZK_Component_Reference/Input/Popup">Popup</a>,
 * if any.
 *
 * @author katherine
 * @see IPopup
 */
public class IPopupRichletTest extends WebDriverTestCase {
	@Test
	public void open() {
		connect("/essential_components/ipopup/open");
		click(jq("@button:contains(Click me to open)"));
		waitResponse();
		assertTrue(jq(".z-popup").exists());
		click(jq("@button:contains(close)"));
		waitResponse();
		assertFalse(jq(".z-popup").isVisible());
		click(jq("@button:contains(x, y)"));
		waitResponse();
		assertEquals(500, jq(".z-popup").offsetLeft());
	}
}