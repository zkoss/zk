/* F96_ZK_4273Test.java

	Purpose:
		
	Description:
		
	History:
		Fri Jul 09 10:45:17 CST 2021, Created by rudyhuang

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.Keys;

import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

/**
 * @author rudyhuang
 */
@Tag("WcagTestOnly")
public class F96_ZK_4273Test extends WebDriverTestCase {
	@Test
	public void testColorbox() {
		connect();

		final JQuery colorbox = jq("@colorbox:eq(0)");
		click(colorbox);
		waitResponse();
		final JQuery colorboxPopup = jq(widget(colorbox).$n("pp"));
		Assertions.assertTrue(colorboxPopup.isVisible(),
				"Popup should be visible");

		getActions().sendKeys(Keys.ARROW_DOWN, Keys.ARROW_RIGHT, Keys.ESCAPE).perform();
		Assertions.assertFalse(colorboxPopup.isVisible(),
				"Popup should be invisible");
	}

	@Test
	public void testColorboxMenu() {
		connect();

		click(jq("@menu:contains(Option)"));
		waitResponse();
		getActions().sendKeys(Keys.ARROW_DOWN).sendKeys(Keys.ARROW_RIGHT).perform();
		final JQuery colorboxPopup = jq(".z-menu-popup.z-colorpalette-popup");
		Assertions.assertTrue(colorboxPopup.isVisible(),
				"Popup should be visible");

		getActions().sendKeys(Keys.ARROW_DOWN, Keys.ARROW_RIGHT, Keys.ESCAPE).perform();
		Assertions.assertFalse(colorboxPopup.isVisible(),
				"Popup should be invisible");
	}

	@Test
	public void testColorboxMenuRoot() {
		connect();

		click(jq("@menubar > @menu > .z-menu-text:contains(Color)"));
		waitResponse();
		final JQuery colorboxPopup = jq(".z-menu-popup.z-colorpalette-popup");
		Assertions.assertTrue(colorboxPopup.isVisible(),
				"Popup should be visible");

		getActions().sendKeys(Keys.ARROW_DOWN, Keys.ARROW_RIGHT, Keys.ESCAPE).perform();
		Assertions.assertFalse(colorboxPopup.isVisible(),
				"Popup should be invisible");
	}
}
