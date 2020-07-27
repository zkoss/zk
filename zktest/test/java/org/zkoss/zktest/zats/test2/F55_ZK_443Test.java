/* F55_ZK_443Test.java

	Purpose:
		
	Description:
		
	History:
		Fri Apr 12 17:33:00 CST 2019, Created by rudyhuang

Copyright (C) 2019 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.Keys;
import org.openqa.selenium.interactions.Actions;

import org.zkoss.zktest.zats.WebDriverTestCase;

/**
 * @author rudyhuang
 */
public class F55_ZK_443Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();

		click(jq("@menu:first"));
		waitResponse();
		Actions actions = getActions();
		actions.sendKeys(Keys.RIGHT).perform();
		waitResponse();
		Assert.assertTrue(jq("@menu:eq(1)").hasClass("z-menu-selected"));

		actions.sendKeys(Keys.DOWN, Keys.ENTER).perform();
		waitResponse();
		Assert.assertEquals(2, jq(".z-menupopup.z-menupopup-open").length());

		actions.sendKeys(Keys.ESCAPE).perform();
		waitResponse();
		Assert.assertEquals(0, jq(".z-menupopup.z-menupopup-open").length());

		actions.sendKeys(Keys.TAB, Keys.TAB, Keys.ENTER).perform();
		waitResponse();
		Assert.assertTrue(jq(".z-messagebox-window").exists());
		actions.sendKeys(Keys.ENTER).perform();
		waitResponse();

		actions.sendKeys(Keys.TAB, Keys.SPACE).perform();
		waitResponse();
		Assert.assertTrue(jq("@checkbox input").is(":checked"));

		actions.sendKeys(Keys.chord(Keys.SHIFT, Keys.TAB), Keys.UP, Keys.RIGHT)
				.sendKeys(Keys.DOWN, Keys.RIGHT)
				.perform();
		waitResponse();
		Assert.assertTrue(jq(".z-menu-popup.z-colorpalette-popup").isVisible());

		click(jq("@button:eq(0)"));
		waitResponse();
		Assert.assertTrue(jq("@menu:contains(Project)").hasClass("z-menu-hover"));

		click(jq("@button:eq(1)"));
		waitResponse();
		Assert.assertTrue(jq("@menu:contains(Clickable)").hasClass("z-menu-selected"));
		Assert.assertTrue(jq("$pp4").isVisible());
	}
}
