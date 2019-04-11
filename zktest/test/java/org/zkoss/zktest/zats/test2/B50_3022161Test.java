/* B50_3022161Test.java

	Purpose:
		
	Description:
		
	History:
		Fri Mar 22 15:38:43 CST 2019, Created by rudyhuang

Copyright (C) 2019 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import java.util.logging.Level;

import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.logging.LogType;

import org.zkoss.zktest.zats.WebDriverTestCase;

/**
 * @author rudyhuang
 */
public class B50_3022161Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();

		click(jq("@button"));
		waitResponse();
		click(jq("@tab:last"));
		waitResponse();

		Assert.assertTrue(jq("@window").isVisible());
		driver.manage().logs().get(LogType.BROWSER).getAll()
				.stream()
				.filter(entry -> entry.getLevel().intValue() >= Level.SEVERE.intValue())
				.findFirst()
				.ifPresent(logEntry -> Assert.fail(logEntry.toString()));
	}
}
