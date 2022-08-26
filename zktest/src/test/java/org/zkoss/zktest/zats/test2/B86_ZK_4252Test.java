/* B86_ZK_4252.java

	Purpose:
		
	Description:
		
	History:
		Wed May 22 12:10:53 CST 2019, Created by rudyhuang

Copyright (C) 2019 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;

import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.Element;

/**
 * @author rudyhuang
 */
public class B86_ZK_4252Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();

		Element real = widget("@timebox").$n("real");
		copyTimestamp("ts1");
		pasteTimebox(real);
		Assertions.assertEquals("1:1:1 PM", jq(real).val());

		copyTimestamp("ts2");
		pasteTimebox(real);
		Assertions.assertEquals("11:11:11 AM", jq(real).val());
	}

	private void copyTimestamp(String tsid) {
		getActions().click(driver.findElement(By.id(tsid))).perform();
		selectAll();
		copy();
	}

	private void pasteTimebox(Element real) {
		click(real);
		selectAll();
		paste();
		blur(real);
		waitResponse();
	}
}
