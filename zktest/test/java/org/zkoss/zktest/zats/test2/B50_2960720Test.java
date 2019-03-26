/* B50_2960720Test.java

	Purpose:
		
	Description:
		
	History:
		Thu Mar 21 18:07:14 CST 2019, Created by rudyhuang

Copyright (C) 2019 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import org.zkoss.zktest.zats.WebDriverTestCase;

/**
 * @author rudyhuang
 */
public class B50_2960720Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();

		String ckeditorId = jq("@ckeditor").attr("id");
		click(jq("@button"));
		waitResponse();
		new WebDriverWait(driver, 5)
				.until(ExpectedConditions.presenceOfElementLocated(jq("@ckeditor iframe")));
		waitForCkEditorReady(ckeditorId, 5000);

		driver.switchTo().frame(toElement(jq("@ckeditor iframe")));
		WebElement body = driver.findElement(By.tagName("body"));
		Assert.assertNotEquals("", body.getText());
	}

	private void waitForCkEditorReady(String ckeditorId, long timeoutMs) {
		long s = System.currentTimeMillis();
		while (!isCkEditorReady(ckeditorId)) {
			sleep(200);
			if (System.currentTimeMillis() - s > timeoutMs) {
				Assert.fail("Test case timeout!");
				break;
			}
		}
	}

	private boolean isCkEditorReady(String id) {
		String instanceId = id + "-cnt";
		return Boolean.valueOf(getEval("CKEDITOR.instances[\"" + instanceId + "\"] && CKEDITOR.instances[\"" + instanceId + "\"].status==\"ready\""));
	}
}
