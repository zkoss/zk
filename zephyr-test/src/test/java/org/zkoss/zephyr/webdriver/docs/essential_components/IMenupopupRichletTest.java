/* IMenupopupRichletTest.java

	Purpose:

	Description:

	History:
		Tue Apr 12 11:44:21 CST 2022, Created by katherine

Copyright (C) 2022 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zephyr.webdriver.docs.essential_components;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import org.zkoss.stateless.sul.IMenupopup;
import org.zkoss.test.webdriver.WebDriverTestCase;

/**
 * A set of unit test for {@link IMenupopup} Java Docs.
 * And also refers to something else on <a href="https://www.zkoss.org/wiki/ZK_Component_Reference/Essential_Components/Menu/Menupopup">Menupopup</a>,
 * if any.
 *
 * @author katherine
 * @see IMenupopup
 */
public class IMenupopupRichletTest extends WebDriverTestCase {
	@Test
	public void menupopup() {
		connect("/essential_components/iMenupopup/menupopup");
		click(jq(".z-menu-icon"));
		waitResponse();
		assertTrue(jq(".z-menupopup").isVisible());
		click(jq(".z-menu-icon"));
		waitResponse();
		assertFalse(jq(".z-menupopup").isVisible());
	}
}