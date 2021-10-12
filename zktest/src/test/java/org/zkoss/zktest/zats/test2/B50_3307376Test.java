/* B50_3307376Test.java

		Purpose:
                
		Description:
                
		History:
				Fri Mar 22 12:27:08 CST 2019, Created by charlesqiu

Copyright (C) 2019 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.Keys;

import org.zkoss.zktest.zats.WebDriverTestCase;
import org.zkoss.zktest.zats.ztl.JQuery;

public class B50_3307376Test extends WebDriverTestCase {

	@Test
	public void test() {
		connect();

		JQuery datebox = jq(".z-datebox-input");
		JQuery label = jq(".z-label");

		sendKeys(datebox, "aa");
		waitResponse();
		sendKeys(datebox, Keys.TAB);
		waitResponse();
		Assert.assertEquals("error1", label.text());

		for (int i = 0; i < 2; i++) {
			sendKeys(datebox, Keys.BACK_SPACE);
			waitResponse();
		}
		sendKeys(datebox, Keys.TAB);
		waitResponse();
		Assert.assertEquals("error2", label.text());

		click(jq(".z-datebox-button"));
		waitResponse();
		click(jq(".z-calendar-cell"));
		waitResponse();
		Assert.assertFalse(label.is(":contains(error3)"));
	}
}
