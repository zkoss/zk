/* IXulElementRichletTest.java

	Purpose:
		
	Description:
		
	History:
		4:12 PM 2021/12/29, Created by jumperchen

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zephyr.webdriver.docs.base_components;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.Keys;

import org.zkoss.test.webdriver.DockerWebDriverTestCase;

/**
 * A set of unit test for {@link org.zkoss.zephyr.zpr.IXulElement} Java Docs.
 * And also refers to something else on <a href="https://www.zkoss.org/wiki/ZK_Component_Reference/Base_Components/XulElement">XulElement</a>,
 * if any.
 * @see org.zkoss.zephyr.zpr.IXulElement
 * @author jumperchen
 */
// Remove to extend DockerWebDriver if the bug has fixed - https://tracker.zkoss.org/browse/ZK-5092
public class IXulElementRichletTest extends DockerWebDriverTestCase {

	@Test
	public void testKeystrokeHandling() {
		connect("/base_components/ixulelement/keystrokeHandling");
		clickAt(jq("@textbox"), 3, 3);
		getActions().keyDown(Keys.CONTROL).sendKeys("a").keyUp(Keys.CONTROL).perform();
		waitResponse();

		// ctrl+a
		assertEquals("65", getZKLog());
		closeZKLog();
		getActions().keyDown(Keys.ALT).sendKeys("c").keyUp(Keys.ALT).perform();
		waitResponse();
		// alt+c
		assertEquals("67", getZKLog());
		closeZKLog();
		clickAt(jq("@datebox"), 3, 3);
		getActions().sendKeys(Keys.F10).perform();
		waitResponse();
		assertEquals("121", getZKLog());
		closeZKLog();
		getActions().keyDown(Keys.CONTROL).sendKeys(Keys.F3).keyUp(Keys.CONTROL).perform();
		waitResponse();
		// ctrl+F3
		assertEquals("114",  getZKLog());
	}

	@Test
	public void testContext() {
		connect("/base_components/ixulelement/context");
		rightClick(jq("@label"));
		waitResponse();
		assertTrue(jq("@popup").isVisible());
		assertEquals("Popup context", jq("@popup @label").text());
		assertFalse(getZKLog().contains("reference=null"));
	}

	@Test
	public void testPopup() {
		connect("/base_components/ixulelement/popup");
		click(jq("@label"));
		waitResponse();
		assertTrue(jq("@popup").isVisible());
		assertEquals("Popup", jq("@popup @label").text());
		assertFalse(getZKLog().contains("reference=null"));
	}

	@Test
	public void testTooltip() {
		connect("/base_components/ixulelement/tooltip");
		mouseOver(jq("@label"));
		waitResponse();
		assertTrue(jq("@popup").isVisible());
		assertEquals("Popup tooltip", jq("@popup @label").text());
		assertFalse(getZKLog().contains("reference=null"));
	}
}
