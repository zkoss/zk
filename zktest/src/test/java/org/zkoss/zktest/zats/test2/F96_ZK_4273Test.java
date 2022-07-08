/* F96_ZK_4273Test.java

	Purpose:
		
	Description:
		
	History:
		Fri Jul 09 10:45:17 CST 2021, Created by rudyhuang

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.Keys;

import org.zkoss.zktest.zats.WebDriverTestCase;
import org.zkoss.zktest.zats.ztl.JQuery;

/**
 * @author rudyhuang
 */
//@Category(WcagTestOnly.class)
public class F96_ZK_4273Test extends WebDriverTestCase {
	@Test
	public void testColorbox() {
		connect();

		final JQuery colorbox = jq("@colorbox:eq(0)");
		click(colorbox);
		waitResponse();
		final JQuery colorboxPopup = jq(widget(colorbox).$n("pp"));
		Assert.assertTrue("Popup should be visible", colorboxPopup.isVisible());

		getActions().sendKeys(Keys.ARROW_DOWN, Keys.ARROW_RIGHT, Keys.ESCAPE).perform();
		Assert.assertFalse("Popup should be invisible", colorboxPopup.isVisible());
	}

	@Test
	public void testColorboxMenu() {
		connect();

		click(jq("@menu:contains(Option)"));
		waitResponse();
		getActions().sendKeys(Keys.ARROW_DOWN).sendKeys(Keys.ARROW_RIGHT).perform();
		final JQuery colorboxPopup = jq(".z-menu-popup.z-colorpalette-popup");
		Assert.assertTrue("Popup should be visible", colorboxPopup.isVisible());

		getActions().sendKeys(Keys.ARROW_DOWN, Keys.ARROW_RIGHT, Keys.ESCAPE).perform();
		Assert.assertFalse("Popup should be invisible", colorboxPopup.isVisible());
	}

	@Test
	public void testColorboxMenuRoot() {
		connect();

		click(jq("@menubar > @menu > .z-menu-text:contains(Color)"));
		waitResponse();
		final JQuery colorboxPopup = jq(".z-menu-popup.z-colorpalette-popup");
		Assert.assertTrue("Popup should be visible", colorboxPopup.isVisible());

		getActions().sendKeys(Keys.ARROW_DOWN, Keys.ARROW_RIGHT, Keys.ESCAPE).perform();
		Assert.assertFalse("Popup should be invisible", colorboxPopup.isVisible());
	}
}
