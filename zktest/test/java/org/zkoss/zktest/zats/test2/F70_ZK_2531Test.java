/* F70_ZK_2531Test.java

	Purpose:
		
	Description:
		
	History:
		Wed Apr 17 17:31:49 CST 2019, Created by rudyhuang

Copyright (C) 2019 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.Keys;

import org.zkoss.zktest.zats.WebDriverTestCase;

/**
 * @author rudyhuang
 */
public class F70_ZK_2531Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();

		getActions().sendKeys(Keys.TAB).perform();
		Assert.assertEquals(
				toElement(jq("@checkbox input")),
				driver.switchTo().activeElement());

		getActions().sendKeys(Keys.TAB).perform();
		Assert.assertEquals(
				toElement(jq("@tree .z-focus-a")),
				driver.switchTo().activeElement());

		getActions().sendKeys(Keys.TAB).perform();
		Assert.assertEquals(
				toElement(jq("@button")),
				driver.switchTo().activeElement());

		getActions().sendKeys(Keys.TAB).perform();
		Assert.assertEquals(
				toElement(jq("@listbox .z-focus-a")),
				driver.switchTo().activeElement());
	}
}
