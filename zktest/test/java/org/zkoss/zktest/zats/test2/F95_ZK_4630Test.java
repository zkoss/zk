/* F95_ZK_4630Test.java

	Purpose:
		
	Description:
		
	History:
		Fri Jul 31 11:49:24 CST 2020, Created by rudyhuang

Copyright (C) 2020 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.Keys;

import org.zkoss.zktest.zats.WebDriverTestCase;

/**
 * @author rudyhuang
 */
public class F95_ZK_4630Test extends WebDriverTestCase {
	@Test
	public void testFocusCannotMoveToBackground() {
		connect();

		click(jq("@button:eq(0)"));
		waitResponse(true);

		click(jq("@textbox:eq(0)"));
		waitResponse();

		String shiftTab = Keys.chord(Keys.SHIFT, Keys.TAB);
		getActions().sendKeys(shiftTab, shiftTab, shiftTab).perform();
		Assert.assertEquals("false", getEval("document.activeElement === jq('@button').get(0)"));
	}

	@Test
	public void testFocusMovingShouldBeCircular() {
		connect();

		click(jq("@button:eq(0)"));
		waitResponse(true);

		click(jq("@textbox:eq(0)"));
		waitResponse();

		getActions().sendKeys(Keys.TAB, Keys.TAB, Keys.TAB).perform();
		Assert.assertEquals("true", getEval("document.activeElement === jq('.z-drawer-close').get(0)"));
	}
}
