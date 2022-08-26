/* B50_3021217Test.java

	Purpose:
		
	Description:
		
	History:
		Fri Mar 22 15:21:19 CST 2019, Created by rudyhuang

Copyright (C) 2019 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import java.util.logging.Level;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.logging.LogType;

import org.zkoss.test.webdriver.WebDriverTestCase;

/**
 * @author rudyhuang
 */
public class B50_3021217Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();

		driver.manage().logs().get(LogType.BROWSER).getAll()
				.stream()
				.filter(entry -> entry.getLevel().intValue() >= Level.SEVERE.intValue())
				.findFirst()
				.ifPresent(logEntry -> Assertions.fail(logEntry.toString()));
	}
}
