/* B50_3010936Test.java

	Purpose:
		
	Description:
		
	History:
		Fri Mar 22 14:36:24 CST 2019, Created by rudyhuang

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
public class B50_3010936Test extends WebDriverTestCase {
	@Override
	protected String getFileExtension() {
		return ".zhtml";
	}

	@Test
	public void test() {
		connect();

		driver.manage().logs().get(LogType.BROWSER).getAll()
				.stream()
				.filter(entry -> entry.getLevel().intValue() >= Level.SEVERE.intValue())
				.findFirst()
				.ifPresent(logEntry -> Assert.fail(logEntry.toString()));
	}
}
