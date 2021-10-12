/* B96_ZK_4852Test.java

	Purpose:
		
	Description:
		
	History:
		Wed Apr 21 12:50:33 CST 2021, Created by jameschu

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.logging.LogType;

import java.util.logging.Level;

import org.zkoss.zktest.zats.WebDriverTestCase;

/**
 * @author jameschu
 */
public class B96_ZK_4852Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		sleep(3000);
		driver.manage().logs().get(LogType.BROWSER).getAll()
				.stream()
				.filter(entry -> entry.getLevel().intValue() >= Level.SEVERE.intValue())
				.findFirst()
				.ifPresent(logEntry -> {
					String logStr = logEntry.toString();
					if (!logStr.contains("myFunc is not defined"))
						Assert.fail("The JS error message should contains 'myFunc is not defined', error: " + logStr);
				});
	}
}
