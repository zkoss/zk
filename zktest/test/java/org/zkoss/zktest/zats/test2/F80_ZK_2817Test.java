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

		testKeyHandler(Keys.chord(Keys.CONTROL, "y"), "Key Pressed: Ctrl + Y");
		testKeyHandler(Keys.chord(Keys.CONTROL, Keys.ALT, "t"), "Key Pressed: Ctrl + Alt + T");
		testKeyHandler(Keys.chord(Keys.CONTROL, Keys.SHIFT, "o"), "Key Pressed: Ctrl + Shift + O");
		testKeyHandler(Keys.chord(Keys.ALT, Keys.SHIFT, "k"), "Key Pressed: Alt + Shift + K");
		testKeyHandler(Keys.chord(Keys.CONTROL, Keys.ALT, Keys.SHIFT, "p"), "Key Pressed: Ctrl + Alt + Shift + P");
	}

	private void testKeyHandler(String keys, String message) {
		getActions().sendKeys(keys).perform();
		waitResponse();
		Assert.assertEquals(message, getZKLog());
		closeZKLog();
	}
}
