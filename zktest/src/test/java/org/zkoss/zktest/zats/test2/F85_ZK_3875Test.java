/* F85_ZK_3875Test.java

		Purpose:
                
		Description:
                
		History:
				Fri Mar 29 10:14:36 CST 2019, Created by charlesqiu

Copyright (C) 2019 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.Keys;
import org.openqa.selenium.interactions.Actions;

import org.zkoss.zktest.zats.WebDriverTestCase;
import org.zkoss.zktest.zats.ztl.JQuery;

public class F85_ZK_3875Test extends WebDriverTestCase {
	private JQuery buttons;

	@Test
	public void test() {
		connect();

		JQuery textbox = jq(".z-textbox");
		buttons = jq(".z-button");

		sendKeys(textbox, Keys.ENTER);
		waitResponse();
		verifyLog("onOK");

		focus(textbox);
		waitResponse();
		new Actions(driver).keyDown(Keys.SHIFT).sendKeys(Keys.ENTER).build().perform();
		waitResponse();
		verifySelectionStart("1");

		click(buttons.eq(0));
		waitResponse();
		sendKeys(textbox, Keys.ENTER);
		waitResponse();
		verifySelectionStart("2");
	}

	private void verifySelectionStart(String log) {
		click(buttons.eq(1));
		waitResponse();
		verifyLog(log);
	}

	private void verifyLog(String log) {
		Assert.assertEquals(log, getZKLog());
		closeZKLog();
	}
}
