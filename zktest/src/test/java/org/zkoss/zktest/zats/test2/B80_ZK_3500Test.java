/* B80_ZK_3500Test.java

		Purpose:
                
		Description:
                
		History:
				Tue Mar 26 17:07:22 CST 2019, Created by charlesqiu

Copyright (C) 2019 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.Keys;

import org.zkoss.zktest.zats.WebDriverTestCase;
import org.zkoss.zktest.zats.ztl.JQuery;

public class B80_ZK_3500Test extends WebDriverTestCase {

	@Test
	public void test() {
		connect();

		JQuery input = jq(".z-chosenbox-input");
		sendKeys(input, "a");
		waitResponse();
		sendKeys(input, Keys.ENTER);
		waitResponse();
		Assert.assertEquals("a", jq(".z-chosenbox-item-content:eq(0)").text());

		sendKeys(input, "aa");
		sendKeys(input, Keys.ENTER);
		waitResponse();
		Assert.assertEquals("aa", jq(".z-chosenbox-item-content:eq(1)").text());
	}
}