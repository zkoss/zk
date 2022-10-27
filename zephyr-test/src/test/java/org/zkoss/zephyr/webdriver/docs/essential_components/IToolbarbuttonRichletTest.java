/* IToolbarbuttonRichletTest.java

	Purpose:

	Description:

	History:
		Tue Apr 19 17:15:04 CST 2022, Created by katherine

Copyright (C) 2022 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zephyr.webdriver.docs.essential_components;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import org.zkoss.stateless.zpr.IToolbarbutton;
import org.zkoss.test.webdriver.WebDriverTestCase;

/**
 * A set of unit test for {@link IToolbarbutton} Java Docs.
 * And also refers to something else on <a href="https://www.zkoss.org/wiki/ZK_Component_Reference/Essential_Components/Toolbarbutton">Toolbarbutton</a>,
 * if any.
 *
 * @author katherine
 * @see IToolbarbutton
 */
public class IToolbarbuttonRichletTest extends WebDriverTestCase {
	@Test
	public void mode() {
		connect("/essential_components/iToolbarbutton/mode");
		click(jq(".z-toolbarbutton:eq(0)"));
		click(jq(".z-toolbarbutton:eq(1)"));
		waitResponse();
		assertTrue(jq(".z-toolbarbutton:eq(0)").hasClass("z-toolbarbutton-checked"));
		click(jq("@button"));
		click(jq(".z-toolbarbutton:eq(0)"));
		click(jq(".z-toolbarbutton:eq(1)"));
		waitResponse();
		assertFalse(jq(".z-toolbarbutton:eq(0)").hasClass("z-toolbarbutton-checked"));
	}

	@Test
	public void checked() {
		connect("/essential_components/iToolbarbutton/checked");
		assertTrue(jq(".z-toolbarbutton-checked").exists());
		click(jq("@button"));
		waitResponse();
		assertFalse(jq(".z-toolbarbutton-checked").exists());
	}
}