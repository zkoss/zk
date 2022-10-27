/* B60_ZK_745Test.java

	Purpose:
		
	Description:
		
	History:
		4:01 PM 2022/10/27, Created by jumperchen

Copyright (C) 2022 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;

import org.zkoss.test.webdriver.WebDriverTestCase;

/**
 * @author jumperchen
 */
public class B60_ZK_745Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		assertEquals("<foo>1</foo><bar>2</bar>", driver.findElement(By.name("custom1")).getDomAttribute("value"));
		assertEquals("1,2", driver.findElement(By.name("custom2")).getDomAttribute("value"));
	}
}
