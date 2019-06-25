/* F60_ZK_448Test.java

	Purpose:
		
	Description:
		
	History:
		Mon Apr 15 11:44:18 CST 2019, Created by rudyhuang

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
public class F60_ZK_448Test extends WebDriverTestCase {
	@Test
	public void testColorBox() {
		connect();

		click(jq("@colorbox"));
		waitResponse();
		testColorPopup();
	}

	@Test
	public void testColorMenu() {
		connect();

		click(jq("@menu:eq(0)"));
		waitResponse();
		click(jq("@menu:eq(1)"));
		waitResponse();
		testColorPopup();
	}

	private void testColorPopup() {
		Actions actions = getActions();
		actions.sendKeys(Keys.DOWN).perform();
		waitResponse();
		String color = jq(".z-colorpalette-input:visible").val();

		actions.sendKeys(Keys.RIGHT).perform();
		waitResponse();
		String color2 = jq(".z-colorpalette-input:visible").val();
		Assert.assertNotEquals(color, color2);

		actions.sendKeys(Keys.DOWN).perform();
		waitResponse();
		String color3 = jq(".z-colorpalette-input:visible").val();
		Assert.assertNotEquals(color2, color3);

		actions.sendKeys(Keys.LEFT).perform();
		waitResponse();
		String color4 = jq(".z-colorpalette-input:visible").val();
		Assert.assertNotEquals(color3, color4);

		actions.sendKeys(Keys.UP).perform();
		waitResponse();
		String color5 = jq(".z-colorpalette-input:visible").val();
		Assert.assertEquals(color, color5);
	}
}
