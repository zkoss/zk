/* B50_ZK_727Test.java

		Purpose:
		
		Description:
		
		History:
				Fri Apr 26 11:55:54 CST 2019, Created by leon

Copyright (C) 2019 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.Keys;
import org.openqa.selenium.interactions.Actions;
import org.zkoss.zktest.zats.WebDriverTestCase;

public class B50_ZK_727Test extends WebDriverTestCase {
	@Test
	public void test() {
		Actions act = new Actions(connect());
		Assert.assertFalse(isZKLogAvailable());
		click(jq("@datebox"));
		waitResponse();
		act.sendKeys("04/26/2019").sendKeys(Keys.TAB).perform();
		waitResponse();
		Assert.assertEquals("customer error message", jq("@errorbox").text().trim());
	}
}
