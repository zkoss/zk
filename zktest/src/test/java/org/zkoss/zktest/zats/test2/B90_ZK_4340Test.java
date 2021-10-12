/* B90_ZK_4340Test.java

	Purpose:
		
	Description:
		
	History:
		Tue Oct 29 16:30:48 CST 2019, Created by rudyhuang

Copyright (C) 2019 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import org.zkoss.zktest.zats.WebDriverTestCase;
import org.zkoss.zktest.zats.ztl.JQuery;

/**
 * @author rudyhuang
 */
public class B90_ZK_4340Test extends WebDriverTestCase {
	@Test
	public void test() throws Exception {
		connect();

		final JQuery textbox = jq("@textbox");
		new WebDriverWait(driver, 2).until(ExpectedConditions.presenceOfElementLocated(textbox));
		Assert.assertTrue(textbox.exists());

		click(textbox);
		sendKeys(textbox, "test");
		click(jq("body"));
		waitResponse();

		Assert.assertThat(getZKLog(), Matchers.startsWith("[InputEvent onChange"));
	}
}
