/* B50_2951182Test.java

	Purpose:
		
	Description:
		
	History:
		Thu Mar 21 14:20:31 CST 2019, Created by rudyhuang

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
public class B50_2951182Test extends WebDriverTestCase {
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
