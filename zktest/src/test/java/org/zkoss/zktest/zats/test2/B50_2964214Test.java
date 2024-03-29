/* B50_2964214Test.java

	Purpose:
		
	Description:
		
	History:
		Thu Mar 21 18:15:16 CST 2019, Created by rudyhuang

Copyright (C) 2019 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;

import java.time.Duration;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

/**
 * @author rudyhuang
 */
public class B50_2964214Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();

		waitResponse();
		new WebDriverWait(driver, Duration.ofSeconds(5))
				.until(ExpectedConditions.presenceOfElementLocated(jq("@ckeditor iframe")));
		driver.switchTo().frame(toElement(jq("@ckeditor iframe")));
		WebElement body = driver.findElement(By.tagName("body"));

		body.click();
		body.sendKeys("aaa");

		driver.switchTo().defaultContent();
		JQuery textbox = jq("@textbox");
		click(textbox);
		waitResponse();

		Assertions.assertNotEquals("", textbox.val());
		assertThat(textbox.val(), not(containsString("_cke_saved_href")));
	}
}
