/* IToolbarRichletTest.java

	Purpose:

	Description:

	History:
		Tue Apr 19 16:24:08 CST 2022, Created by katherine

Copyright (C) 2022 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zephyr.webdriver.docs.essential_components;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import org.zkoss.stateless.sul.IToolbar;
import org.zkoss.test.webdriver.WebDriverTestCase;

/**
 * A set of unit test for {@link IToolbar} Java Docs.
 * And also refers to something else on <a href="https://www.zkoss.org/wiki/ZK_Component_Reference/Essential_Components/Toolbar">Toolbar</a>,
 * if any.
 *
 * @author katherine
 * @see IToolbar
 */
public class IToolbarRichletTest extends WebDriverTestCase {
	@Test
	public void defaultMold() {
		connect("/essential_components/itoolbar/mold/default");
		assertFalse(jq(".z-toolbar-panel").exists());
	}

	@Test
	public void panelMold() {
		connect("/essential_components/itoolbar/mold/panel");
		assertTrue(jq(".z-toolbar-panel").exists());
	}

	@Test
	public void align() {
		connect("/essential_components/itoolbar/align");
		assertTrue(jq(".z-toolbar-center").exists());
		click(jq("@button"));
		waitResponse();
		assertTrue(jq(".z-toolbar-start").exists());
	}

	@Test
	public void orient() {
		connect("/essential_components/itoolbar/orient");
		assertTrue(jq(".z-toolbar-vertical").exists());
		click(jq("@button"));
		waitResponse();
		assertFalse(jq(".z-toolbar-vertical").exists());
	}

	@Test
	public void overflowPopup() {
		connect("/essential_components/itoolbar/overflowPopup");
		assertTrue(jq(".z-toolbar-overflowpopup-button").exists());
		click(jq("@button"));
		waitResponse();
		assertFalse(jq(".z-toolbar-overflowpopup-button").exists());
	}

	@Test
	public void overflowPopupIconSclass() {
		connect("/essential_components/itoolbar/overflowPopupIconSclass");
		assertTrue(jq(".z-toolbar-overflowpopup-button").hasClass("z-icon-home"));
		click(jq("@button"));
		waitResponse();
		assertTrue(jq(".z-toolbar-overflowpopup-button").hasClass("z-icon-user"));
	}
}