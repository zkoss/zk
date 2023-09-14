/* B96_ZK_5026Test.java

	Purpose:
		
	Description:
		
	History:
		2:03 PM 2021/11/15, Created by jumperchen

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.Keys;
import org.openqa.selenium.interactions.Actions;

import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

/**
 * @author jumperchen
 */
public class B96_ZK_5026Test extends WebDriverTestCase {
	@Test
	public void test() {
		// The highlight class names were used to be all `-hover`, after ZK-5025, it's divided into `-hover` (mouse) and `-focus` (keydown)

		Actions act = new Actions(connect());
		act.moveToElement(toElement(jq("$menu"))).perform();
		waitResponse();

		assertTrue(jq(".z-menupopup-open").isVisible());

		act.moveToElement(toElement(
						jq(".z-menupopup-content > .z-menuitem:first-child").eq(0)))
				.perform();
		assertTrue(jq(" .z-menuitem.z-menuitem-hover").exists());

		act.sendKeys(Keys.DOWN).perform();
		assertTrue(jq(".z-menupopup-content > .z-menuitem:nth-child(2)").hasClass("z-menuitem-focus"));

		act.sendKeys(Keys.DOWN).perform();
		assertTrue(jq(".z-menupopup-content > .z-menuitem:nth-child(3)").hasClass("z-menuitem-focus"));

		// error happen if bug exists
		act.moveToElement(toElement(
						jq(".z-menupopup-content > .z-menu").eq(0)))
				.perform();

		assertEquals(2, jq(".z-menupopup-open").length());

		act.moveToElement(toElement(
						jq(".z-menupopup-content > .z-menuitem:first-child").eq(0)))
				.perform();

		assertTrue(jq(" .z-menuitem.z-menuitem-hover").exists());

		// After ZK-5025, if hover or focus is removed from the menupopup, the menupopup will automatically close.
		// Since the behavior has changed, it's contrary to this test case, so the previous subsequent operations are removed.
	}
}
