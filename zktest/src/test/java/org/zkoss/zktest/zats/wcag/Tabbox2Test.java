/* TabboxTest.java

	Purpose:
		
	Description:
		
	History:
		Thu Jun 11 12:52:22 CST 2020, Created by rudyhuang

Copyright (C) 2020 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.wcag;

import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.Keys;
import org.openqa.selenium.interactions.Actions;

/**
 * @author rudyhuang
 */
public class Tabbox2Test extends WcagTestCase {
	@Test
	public void test() {
		connect();

		click(jq("@textbox:first"));
		waitResponse();

		Actions actions = getActions();
		actions.sendKeys(Keys.TAB, Keys.ARROW_RIGHT, Keys.ARROW_RIGHT, Keys.ENTER).perform();
		waitResponse();
		actions.sendKeys(Keys.DELETE).perform();
		waitResponse();
		Assert.assertEquals("Tab 4", jq(".z-tab-selected").text());

		actions.sendKeys(Keys.DELETE).perform();
		waitResponse();
		Assert.assertEquals("Tab 2", jq(".z-tab-selected").text());
	}
}
