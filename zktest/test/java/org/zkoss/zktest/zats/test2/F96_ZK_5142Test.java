/* F96_ZK_5142Test.java

        Purpose:
                
        Description:
                
        History:
                Thu Jan 25 11:17:49 CST 2024, Created by jamson

Copyright (C) 2024 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

import java.util.logging.Level;

import org.hamcrest.CoreMatchers;
import org.junit.Test;
import org.openqa.selenium.logging.LogType;

import org.zkoss.zktest.zats.WebDriverTestCase;

public class F96_ZK_5142Test extends WebDriverTestCase {
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
