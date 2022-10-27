/* IBandpopupRichletTest.java

	Purpose:

	Description:

	History:
		Mon Apr 11 13:05:34 CST 2022, Created by katherine

Copyright (C) 2022 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zephyr.webdriver.docs.input;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import org.zkoss.stateless.zpr.IBandpopup;
import org.zkoss.test.webdriver.WebDriverTestCase;

/**
 * A set of unit test for {@link IBandpopup} Java Docs.
 * And also refers to something else on <a href="https://www.zkoss.org/wiki/ZK_Component_Reference/Input/Bandpopup">Bandpopup</a>,
 * if any.
 *
 * @author katherine
 * @see IBandpopup
 */
public class IBandpopupRichletTest extends WebDriverTestCase {
	@Test
	public void bandpopup() {
		connect("/input/iBandpopup/default");
		click(jq(".z-bandbox-button"));
		assertTrue(jq(".z-bandbox-popup .z-textbox").exists());
		click(jq("@button"));
		waitResponse();
		assertTrue(jq(".z-bandbox-popup .z-label").exists());
	}
}