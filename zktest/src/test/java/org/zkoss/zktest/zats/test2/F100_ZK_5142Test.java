/* F100_ZK_5142Test.java

	Purpose:
		
	Description:
		
	History:
		6:15 PM 2023/7/26, Created by jumperchen

Copyright (C) 2023 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.logging.Level;

import org.hamcrest.CoreMatchers;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.logging.LogType;

import org.zkoss.test.webdriver.WebDriverTestCase;

/**
 * @author jumperchen
 */
public class F100_ZK_5142Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		waitResponse();

		// should have JS error about Failed to load resource: the server responded with a status of 404 (Not Found)
		driver.manage().logs().get(LogType.BROWSER).getAll().stream()
				.filter(entry -> entry.getLevel().intValue() >= Level.SEVERE.intValue()).findFirst()
				.ifPresent(log -> assertThat(log.toString(), CoreMatchers.containsString("Failed to load resource: the server responded with a status of 404")));
		assertEquals("null", getZKLog());
	}
}
