/* B95_ZK_4754Test.java

	Purpose:
		
	Description:
		
	History:
		Tue Dec 22 16:42:22 CST 2020, Created by rudyhuang

Copyright (C) 2020 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import org.zkoss.zktest.zats.WebDriverTestCase;

/**
 * @author rudyhuang
 */
public class B95_ZK_4754Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();

		final WebDriverWait wait = new WebDriverWait(driver, 3);
		wait.until(ExpectedConditions.attributeToBeNotEmpty(toElement(jq("canvas")), "height"));
		sleep(500);

		click(jq("@button:eq(0)"));
		sleep(200); // should faster than page finished loading
		click(jq("@button:eq(1)"));
		waitResponse();
		Assert.assertFalse(hasError());
	}
}
