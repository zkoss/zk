/* IColumnsRichletTest.java

	Purpose:

	Description:

	History:
		Fri Apr 01 12:16:30 CST 2022, Created by katherine

Copyright (C) 2022 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.stateless.webdriver.docs.data;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.interactions.Actions;

import org.zkoss.stateless.sul.IColumns;
import org.zkoss.test.webdriver.WebDriverTestCase;

/**
 * A set of unit test for {@link IColumns} Java Docs.
 * And also refers to something else on <a href="https://www.zkoss.org/wiki/ZK_Component_Reference/Data/Grid/Columns">Columns</a>,
 * if any.
 *
 * @author katherine
 * @see IColumns
 */
public class IColumnsRichletTest extends WebDriverTestCase {
	@Test
	public void columnsgroup() {
		connect("/data/iColumns/columnsgroup");
		openMenuPopup();
		assertFalse(jq(".z-columns-menuungrouping").exists());
		click(jq("@button"));
		waitResponse();
		openMenuPopup();
		assertTrue(jq(".z-columns-menuungrouping").exists());
	}

	@Test
	public void columnshide() {
		connect("/data/iColumns/columnshide");
		openMenuPopup();
		assertFalse(jq(".z-menuitem-checkable").exists());
		click(jq("@button"));
		openMenuPopup();
		assertTrue(jq(".z-menuitem-checkable").exists());
	}

	@Test
	public void menupopup() {
		connect("/data/iColumns/menupopup");
		assertTrue(jq(".z-column-button").exists());
		click(jq("@button"));
		waitResponse();
		assertFalse(jq(".z-column-button").exists());
	}

	private void openMenuPopup() {
		Actions act = getActions();
		act.moveToElement(toElement(jq(".z-column:eq(0)"))).build().perform();
		waitResponse();
		click(jq(".z-column-button"));
		waitResponse();
	}
}