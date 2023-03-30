/* F85_ZK_3711_1Test.java

	Purpose:
		
	Description:
		
	History:
		Thu Jul 27 14:10:06 CST 2017, Created by rudyhuang

Copyright (C) 2017 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zephyr.webdriver.test2;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;

import org.zkoss.test.webdriver.WebDriverTestCase;

/**
 * @author rudyhuang
 */
public class F85_ZK_3711_1Test extends WebDriverTestCase {
	@Test
	public void testHistoryPopStateMoreThanOne() throws Exception {
		connect();
		sleep(2000);
		assertTrue(getWebDriver().findElement(By.tagName("html")).getText().contains("org.zkoss.zk.ui.UiException: more than one [@HistoryPopState]"));
	}
}
