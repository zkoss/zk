/* B86_ZK_3752Test.java

		Purpose:
		
		Description:
		
		History:
				Wed Nov 21 11:17:54 CST 2018, Created by leon

Copyright (C) 2018 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;

import org.zkoss.test.webdriver.WebDriverTestCase;

public class B86_ZK_3752Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		click(jq("$findMap"));
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		String sourcemap = driver.findElement(By.tagName("body")).getText();
		Assertions.assertFalse(sourcemap.contains("Error") || sourcemap.length() == 0);
	}
}
