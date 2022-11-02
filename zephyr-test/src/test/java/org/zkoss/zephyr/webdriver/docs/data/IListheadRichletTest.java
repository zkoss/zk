/* IListheadRichletTest.java

	Purpose:

	Description:

	History:
		Fri Apr 08 18:08:51 CST 2022, Created by katherine

Copyright (C) 2022 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zephyr.webdriver.docs.data;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.interactions.Actions;

import org.zkoss.stateless.sul.IListhead;
import org.zkoss.test.webdriver.WebDriverTestCase;

/**
 * A set of unit test for {@link IListhead} Java Docs.
 * And also refers to something else on <a href="https://www.zkoss.org/wiki/ZK_Component_Reference/Data/Listbox/Listhead">Listhead</a>,
 * if any.
 *
 * @author katherine
 * @see IListhead
 */
public class IListheadRichletTest extends WebDriverTestCase {
	@Test
	public void columnsgroup() {
		connect("/data/iListhead/columnsgroup");
		openMenuPopup();
		assertFalse(jq(".z-listhead-menuungrouping").exists());
		click(jq("@button"));
		waitResponse();
		openMenuPopup();
		assertTrue(jq(".z-listhead-menuungrouping").exists());
	}

	@Test
	public void columnshide() {
		connect("/data/iListhead/columnshide");
		openMenuPopup();
		assertFalse(jq(".z-menuitem-checkable").exists());
		click(jq("@button"));
		openMenuPopup();
		assertTrue(jq(".z-menuitem-checkable").exists());
	}

	@Test
	public void menupopup() {
		connect("/data/iListhead/menupopup");
		assertTrue(jq(".z-listheader-button").exists());
		click(jq("@button"));
		waitResponse();
		assertFalse(jq(".z-listheader-button").exists());
	}

	private void openMenuPopup() {
		Actions act = getActions();
		act.moveToElement(toElement(jq(".z-listheader:eq(0)"))).build().perform();
		waitResponse();
		click(jq(".z-listheader-button"));
		waitResponse();
	}
}