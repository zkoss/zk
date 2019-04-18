/* F50_3323594Test.java

	Purpose:
		
	Description:
		
	History:
		Fri Apr 12 14:31:43 CST 2019, Created by rudyhuang

Copyright (C) 2019 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.Alert;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import org.zkoss.zktest.zats.WebDriverTestCase;

/**
 * @author rudyhuang
 */
public class F50_3323594Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();

		click(jq("@button"));
		Alert alert = new WebDriverWait(driver, 3).until(ExpectedConditions.alertIsPresent());
		Assert.assertEquals("count1: 30, count2: 30", alert.getText());
	}
}
