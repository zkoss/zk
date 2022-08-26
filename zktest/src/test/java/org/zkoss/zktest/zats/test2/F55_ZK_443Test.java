/* F55_ZK_443Test.java

	Purpose:
		
	Description:
		
	History:
		Fri Apr 12 17:33:00 CST 2019, Created by rudyhuang

Copyright (C) 2019 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.Keys;
import org.openqa.selenium.interactions.Actions;

import org.zkoss.test.webdriver.WebDriverTestCase;

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
		Assertions.assertTrue(jq("@menu:eq(1)").hasClass("z-menu-selected"));

		actions.sendKeys(Keys.DOWN, Keys.ENTER).perform();
		waitResponse();
		Assertions.assertEquals(2, jq(".z-menupopup.z-menupopup-open").length());

		actions.sendKeys(Keys.ESCAPE).perform();
		waitResponse();
		Assertions.assertEquals(0, jq(".z-menupopup.z-menupopup-open").length());

		actions.sendKeys(Keys.TAB, Keys.TAB, Keys.ENTER).perform();
		waitResponse();
		Assertions.assertTrue(jq(".z-messagebox-window").exists());
		actions.sendKeys(Keys.ENTER).perform();
		waitResponse();

		actions.sendKeys(Keys.TAB, Keys.SPACE).perform();
		waitResponse();
		Assertions.assertTrue(jq("@checkbox input").is(":checked"));

		actions.keyDown(Keys.SHIFT).sendKeys(Keys.TAB).keyUp(Keys.SHIFT)
				.sendKeys(Keys.UP, Keys.RIGHT, Keys.DOWN, Keys.RIGHT)
				.perform();
		waitResponse();
		Assertions.assertTrue(jq(".z-menu-popup.z-colorpalette-popup").isVisible());

		click(jq("@button:eq(0)"));
		waitResponse();
		Assertions.assertTrue(jq("@menu:contains(Project)").hasClass("z-menu-hover"));

		click(jq("@button:eq(1)"));
		waitResponse();
		Assertions.assertTrue(jq("@menu:contains(Clickable)").hasClass("z-menu-selected"));
		Assertions.assertTrue(jq("$pp4").isVisible());
	}
}
