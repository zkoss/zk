/* F80_ZK_2817Test.java

	Purpose:
		
	Description:
		
	History:
		Thu Apr 18 09:52:42 CST 2019, Created by rudyhuang

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
public class F80_ZK_2817Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();

		click(jq("@textbox"));
		waitResponse();

		final Keys[] ctrl = { Keys.CONTROL };
		final Keys[] ctrlAlt = { Keys.CONTROL, Keys.ALT };
		final Keys[] ctrlShift = { Keys.CONTROL, Keys.SHIFT };
		final Keys[] altShift = { Keys.ALT, Keys.SHIFT };
		final Keys[] ctrlAltShift = { Keys.CONTROL, Keys.ALT, Keys.SHIFT };
		testKeyHandler(ctrl, "y", "Key Pressed: Ctrl + Y");
		testKeyHandler(ctrlAlt, "t", "Key Pressed: Ctrl + Alt + T");
		testKeyHandler(ctrlShift, "o", "Key Pressed: Ctrl + Shift + O");
		testKeyHandler(altShift, "k", "Key Pressed: Alt + Shift + K");
		testKeyHandler(ctrlAltShift, "p", "Key Pressed: Ctrl + Alt + Shift + P");
	}

	private void testKeyHandler(Keys[] modifiers, String keys, String message) {
		Actions actions = getActions();
		for (Keys modifier : modifiers) {
			actions = actions.keyDown(modifier);
		}
		actions = actions.sendKeys(keys);
		for (Keys modifier : modifiers) {
			actions = actions.keyUp(modifier);
		}
		actions.perform();
		waitResponse();
		Assert.assertEquals(message, getZKLog());
		closeZKLog();
	}
}
