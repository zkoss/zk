/* IBandboxRichletTest.java

	Purpose:

	Description:

	History:
		Thu Feb 24 16:14:38 CST 2022, Created by katherine

Copyright (C) 2022 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zephyr.webdriver.stateless.docs.input;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.Keys;
import org.openqa.selenium.interactions.Actions;

import org.zkoss.stateless.sul.IBandbox;
import org.zkoss.test.webdriver.WebDriverTestCase;

/**
 * A set of unit test for {@link IBandbox} Java Docs.
 * And also refers to something else on <a href="https://www.zkoss.org/wiki/ZK_Component_Reference/Input/Bandbox">Bandbox</a>,
 * if any.
 *
 * @author katherine
 * @see IBandbox
 */
public class IBandboxRichletTest extends WebDriverTestCase {
	@Test
	public void autodrop() {
		connect("/input/iBandbox/autodrop");
		Actions act = getActions();
		act.sendKeys(Keys.TAB).sendKeys(Keys.TAB).sendKeys("b").perform();
		waitResponse();
		assertFalse(jq(".z-bandbox-popup").exists());
		act.sendKeys(Keys.TAB).sendKeys("b").perform();
		waitResponse();
		assertTrue(jq(".z-bandbox-popup").exists());
		click(jq("@button"));
		waitResponse();
		act.sendKeys(Keys.TAB).sendKeys(Keys.TAB).sendKeys("b").perform();
		assertFalse(jq(".z-bandbox-popup").isVisible());
	}

	@Test
	public void buttonVisible() {
		connect("/input/iBandbox/buttonVisible");
		assertTrue(jq(".z-bandbox-button:eq(0)").isVisible());
		assertFalse(jq(".z-bandbox-button:eq(1)").isVisible());
		click(jq("@button"));
		waitResponse();
		assertTrue(jq(".z-bandbox-button:eq(1)").isVisible());
	}

	@Test
	public void iconSclass() {
		connect("/input/iBandbox/iconSclass");
		assertTrue(jq(".z-icon-home").exists());
		click(jq("@button"));
		waitResponse();
		assertTrue(jq(".z-icon-user").exists());
	}

	@Test
	public void open() {
		connect("/input/iBandbox/open");
		assertEquals("bandpopup2", jq(".z-bandbox-popup").text());
		click(jq("@button"));
		waitResponse();
		assertFalse(jq(".z-bandbox-popup").isVisible());
	}

	@Test
	public void popupWidth() {
		connect("/input/iBandbox/popupWidth");
		click(jq(".z-bandbox-button"));
		waitResponse();
		assertEquals(100, jq(".z-bandbox-popup").outerWidth());
		click(jq("@button"));
		waitResponse();
		click(jq(".z-bandbox-button"));
		waitResponse();
		assertEquals(200, jq(".z-bandbox-popup").outerWidth());
	}
}