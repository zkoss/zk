/* B50_2947657Test.java

	Purpose:
		
	Description:
		
	History:
		Wed Mar 20 18:10:58 CST 2019, Created by rudyhuang

Copyright (C) 2019 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import org.zkoss.zktest.zats.WebDriverTestCase;
import org.zkoss.zktest.zats.ztl.JQuery;

/**
 * @author rudyhuang
 */
public class B50_2947657Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();

		type(jq("@textbox"), "ZK");
		type(jq("@combobox input"), "Engineer");
		click(jq("@listitem:eq(3)"));
		driver.findElement(By.cssSelector("input[type='submit']")).click();

		WebDriverWait wait = new WebDriverWait(driver, 5);
		wait.until(ExpectedConditions.presenceOfElementLocated(By.tagName("body")));
		waitResponse();

		JQuery labels = jq("@div @label");
		Assert.assertEquals("Engineer", labels.eq(1).html());
		Assert.assertEquals("ZK", labels.eq(3).html());
		Assert.assertEquals("4", labels.eq(5).html());
	}
}
