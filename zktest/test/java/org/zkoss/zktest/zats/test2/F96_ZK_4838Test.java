/* F96_ZK_4838Test.java

		Purpose:
		
		Description:
		
		History:
				Thu Apr 15 10:00:22 CST 2021, Created by jameschu

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.Test;
import org.openqa.selenium.Keys;
import org.openqa.selenium.interactions.Actions;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import org.zkoss.zktest.zats.WebDriverTestCase;
import org.zkoss.zktest.zats.ztl.JQuery;

public class F96_ZK_4838Test extends WebDriverTestCase {

	@Test
	public void test() {
		connect();
		JQuery button = jq("@button").eq(0);
		click(button);
		waitResponse();
		Actions action = getActions();
		//test tab
		action.sendKeys(Keys.TAB).perform();
		waitResponse();
		assertEquals("searchbox focus", getZKLog());
		closeZKLog();
		action.sendKeys(Keys.ARROW_DOWN).perform();
		waitResponse();
		action.sendKeys("s").perform();
		waitResponse();
		action.sendKeys(Keys.ARROW_DOWN).perform();
		waitResponse();
		action.sendKeys(Keys.ENTER).perform();
		waitResponse();
		action.sendKeys(Keys.BACK_SPACE).perform();
		waitResponse();
		assertFalse(isZKLogAvailable());
		action.sendKeys(Keys.TAB).perform();
		waitResponse();
		assertEquals("searchbox blur", getZKLog());
		closeZKLog();
		action.sendKeys(Keys.TAB).perform();
		waitResponse();
		assertEquals("cascader focus", getZKLog());
		closeZKLog();
		action.sendKeys(Keys.ARROW_DOWN).perform();
		waitResponse();
		action.sendKeys(Keys.ARROW_DOWN).perform();
		waitResponse();
		action.sendKeys(Keys.ARROW_RIGHT).perform();
		waitResponse();
		action.sendKeys(Keys.ARROW_DOWN).perform();
		waitResponse();
		action.sendKeys(Keys.ENTER).perform();
		waitResponse();
		action.sendKeys(Keys.BACK_SPACE).perform();
		waitResponse();
		assertFalse(isZKLogAvailable());
		action.sendKeys(Keys.TAB).perform();
		waitResponse();
		assertEquals("cascader blur", getZKLog());
	}
}
