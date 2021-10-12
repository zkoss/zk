/* B50_2960720Test.java

	Purpose:
		
	Description:
		
	History:
		Thu Mar 21 18:07:14 CST 2019, Created by rudyhuang

Copyright (C) 2019 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.containsString;

import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import org.zkoss.zktest.zats.WebDriverTestCase;

/**
 * @author rudyhuang
 */
public class B50_2960720Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();

		String ckeditorId = widget("@ckeditor").uuid();
		waitForCkEditorReady(ckeditorId, 3000);
		click(jq("@button"));
		waitResponse();
		waitForCkEditorReady(ckeditorId, 3000);

		driver.switchTo().frame(toElement(jq("@ckeditor iframe")));
		WebElement body = driver.findElement(By.tagName("body"));
		Assert.assertThat(body.getText(), allOf(containsString("Jone"), containsString("Mary")));
	}

	private void waitForCkEditorReady(String ckeditorId, long timeoutMs) {
		long s = System.currentTimeMillis();
		while (!isCkEditorReady(ckeditorId)) {
			sleep(200);
			if (System.currentTimeMillis() - s > timeoutMs) {
				break;
			}
		}
	}

	private boolean isCkEditorReady(String id) {
		String instanceId = id + "-cnt";
		return Boolean.valueOf(getEval("CKEDITOR.instances[\"" + instanceId + "\"] && CKEDITOR.instances[\"" + instanceId + "\"].status==\"ready\""));
	}
}
