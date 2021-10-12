/* B86_ZK_4079Test.java

        Purpose:
                
        Description:
                
        History:
                Fri Oct 19 11:16:31 CST 2018, Created by leon

Copyright (C) 2018 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.Alert;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.zkoss.zktest.zats.WebDriverTestCase;

public class B86_ZK_4079Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		click(jq("$low"));
		waitResponse();
		Assert.assertEquals(getBarWidth(), jq("$prog").width(), 1);
		
		click(jq("$reset"));
		waitResponse();
		
		click(jq("$high"));
		waitResponse();
		Assert.assertTrue(getBarWidth() >= 800);
		
		click(jq("$reset"));
		waitResponse();
		
		click(jq("$veryhigh"));
		waitResponse();
		Assert.assertTrue(getBarWidth() >= 800);
	}
	
	private Double getBarWidth() {
		WebDriverWait wait = new WebDriverWait(driver, 7);
		wait.until(ExpectedConditions.alertIsPresent());
		Alert alert = driver.switchTo().alert();
		Double barWidth = Double.valueOf(alert.getText().split(": ")[1]);
		System.out.println(barWidth);
		alert.accept();
		return barWidth;
	}
}
