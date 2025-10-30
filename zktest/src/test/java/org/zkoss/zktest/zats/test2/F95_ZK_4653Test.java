/* F95_ZK_4653Test.java

		Purpose:
		
		Description:
		
		History:
				Wed Sep 02 14:47:27 CST 2020, Created by leon

Copyright (C) 2020 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.Keys;
import org.openqa.selenium.interactions.Actions;

import org.zkoss.test.webdriver.WebDriverTestCase;

public class F95_ZK_4653Test extends WebDriverTestCase {
	@Test
	public void oneKeyTest() {
		connect();
		Actions actions = getActions();

		click(jq("@button").eq(0));
		waitResponse();

		actions.keyDown(Keys.ALT).keyUp(Keys.ALT).perform();
		waitResponse();
		Assertions.assertTrue(getZKLog().contains("alt key is pressed"));
		closeZKLog();

		actions.sendKeys(Keys.SPACE).perform();
		waitResponse();
		Assertions.assertFalse(getZKLog().contains("alt key is pressed"));
		closeZKLog();

		click(jq("@button").eq(1));
		waitResponse();

		actions.sendKeys("a").perform();
		waitResponse();
		Assertions.assertTrue(getZKLog().contains("a key is pressed"));
		closeZKLog();

		actions.sendKeys(Keys.SPACE).perform();
		waitResponse();
		Assertions.assertFalse(getZKLog().contains("a key is pressed"));
		closeZKLog();
	}

	@Test
	public void comboKeysTest() {
		connect();
		Actions actions = getActions();

		click(jq("@button").eq(2));
		waitResponse();

		// Modifier keys (like ctrl,shift etc) are not released with sendKeys
		actions.keyDown(Keys.ALT).sendKeys(Keys.ARROW_DOWN).keyUp(Keys.ALT).perform();
		waitResponse();
		Assertions.assertTrue(getZKLog().contains("alt + ArrowDown keys is pressed"));
		closeZKLog();

		actions.keyDown(Keys.ALT).sendKeys(Keys.ARROW_UP).keyUp(Keys.ALT).perform();
		waitResponse();
		Assertions.assertFalse(getZKLog().contains("alt + ArrowDown keys is pressed"));
		closeZKLog();

		click(jq("@button").eq(3));
		waitResponse();

		actions.keyDown(Keys.CONTROL).keyDown(Keys.ALT).sendKeys(Keys.ARROW_DOWN).keyUp(Keys.CONTROL).keyUp(Keys.ALT).perform();
		waitResponse();
		Assertions.assertTrue(getZKLog().contains("ctrl + alt + ArrowDown is pressed"));
		closeZKLog();

		actions.keyDown(Keys.CONTROL).keyDown(Keys.SHIFT).sendKeys(Keys.ARROW_DOWN).keyUp(Keys.CONTROL).keyUp(Keys.SHIFT).perform();
		waitResponse();
		Assertions.assertFalse(getZKLog().contains("ctrl + alt + ArrowDown is pressed"));
		closeZKLog();

		click(jq("@button").eq(4));
		waitResponse();

		actions.keyDown(Keys.CONTROL).keyDown(Keys.META).sendKeys("a").keyUp(Keys.CONTROL).keyUp(Keys.META).perform();
		waitResponse();
		Assertions.assertTrue(getZKLog().contains("ctrl + meta + a keys is pressed"));
		closeZKLog();

		actions.keyDown(Keys.CONTROL).keyDown(Keys.META).sendKeys("z").keyUp(Keys.CONTROL).keyUp(Keys.META).perform();
		waitResponse();
		Assertions.assertFalse(getZKLog().contains("ctrl + meta + a keys is pressed"));
		closeZKLog();
	}

	@Test
	public void invalidComboKeysTest() {
		connect();
		Actions actions = getActions();

		click(jq("@button").eq(5));
		waitResponse();

		actions.sendKeys(Keys.chord(Keys.ARROW_UP, Keys.ARROW_DOWN)).perform();
		waitResponse();
		Assertions.assertFalse(isZKLogAvailable());
		closeZKLog();

		actions.keyDown(Keys.ALT).sendKeys(Keys.ARROW_UP).keyUp(Keys.ALT).perform();
		waitResponse();
		Assertions.assertFalse(isZKLogAvailable());
		closeZKLog();
	}
}
