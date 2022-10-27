/* IComboboxRichletTest.java

	Purpose:

	Description:

	History:
		Tue Mar 01 18:14:37 CST 2022, Created by katherine

Copyright (C) 2022 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zephyr.webdriver.docs.input;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.Keys;
import org.openqa.selenium.interactions.Actions;

import org.zkoss.stateless.zpr.ICombobox;
import org.zkoss.test.webdriver.WebDriverTestCase;

/**
 * A set of unit test for {@link ICombobox} Java Docs.
 * And also refers to something else on <a href="https://www.zkoss.org/wiki/ZK_Component_Reference/Input/Combobox">Combobox</a>,
 * if any.
 *
 * @author katherine
 * @see ICombobox
 */
public class IComboboxRichletTest extends WebDriverTestCase {
	@Test
	public void autocomplete() {
		connect("/input/iCombobox/autocomplete");
		Actions act = getActions();
		act.sendKeys(Keys.TAB).sendKeys("i").perform();
		waitResponse();
		assertEquals("i", jq(".z-combobox-input").val());
		click(jq("@button"));
		waitResponse();
		click(jq("@combobox"));
		waitResponse();
		act.sendKeys("t").perform();
		waitResponse();
		assertEquals("item 1", jq(".z-combobox-input").val());
	}

	@Test
	public void autodrop() {
		connect("/input/iCombobox/autodrop");
		Actions act = getActions();
		act.sendKeys(Keys.TAB).sendKeys("a").perform();
		waitResponse();
		assertTrue(jq(".z-combobox-popup.z-combobox-open").exists());
		click(jq("@button"));
		waitResponse();
		act.sendKeys(Keys.TAB).sendKeys("a").perform();
		waitResponse();
		assertFalse(jq(".z-combobox-popup.z-combobox-open").exists());
	}

	@Test
	public void buttonVisible() {
		connect("/input/iCombobox/buttonVisible");
		assertFalse(jq(".z-combobox-button").isVisible());
		click(jq("@button"));
		waitResponse();
		assertTrue(jq(".z-combobox-button").isVisible());
	}

	@Test
	public void emptySearchMessage() {
		connect("/input/iCombobox/emptySearchMessage");
		type(jq(".z-combobox-input"), "a");
		waitResponse();
		assertEquals("no result", jq(".z-combobox-empty-search-message").text());
		click(jq("@button"));
		waitResponse();
		type(jq(".z-combobox-input"), "a");
		waitResponse();
		assertEquals("no result2", jq(".z-combobox-empty-search-message").text());
	}

	@Test
	public void iconSclass() {
		connect("/input/iCombobox/iconSclass");
		assertTrue(jq(".z-icon-user").exists());
		click(jq("@button"));
		waitResponse();
		assertTrue(jq(".z-icon-home").exists());
	}

	@Test
	public void instantSelect() {
		connect("/input/iCombobox/instantSelect");
		Actions act = getActions();
		click(jq(".z-combobox-button"));
		waitResponse();
		act.sendKeys(Keys.ARROW_DOWN).perform();
		waitResponse();
		assertFalse(jq("#zk_log").exists());
		click(jq("@button"));
		closeZKLog();
		waitResponse();
		click(jq(".z-combobox-button"));
		waitResponse();
		act.sendKeys(Keys.ARROW_DOWN).perform();
		assertEquals("select", getZKLog());
	}

	@Test
	public void open() {
		connect("/input/iCombobox/open");
		assertFalse(jq(".z-combobox-popup").exists());
		click(jq("@button"));
		waitResponse();
		assertTrue(jq(".z-combobox-popup").exists());
	}

	@Test
	public void popupWidth() {
		connect("/input/iCombobox/popupWidth");
		assertEquals(100, jq(".z-combobox-popup").outerWidth());
		click(jq("@button"));
		waitResponse();
		assertEquals(200, jq(".z-combobox-popup").outerWidth());
	}
}