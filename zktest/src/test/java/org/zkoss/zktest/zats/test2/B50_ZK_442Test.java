/* B50_ZK_442Test.java

		Purpose:
		
		Description:
		
		History:
				Tue Apr 09 16:14:38 CST 2019, Created by leon

Copyright (C) 2019 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.Keys;
import org.openqa.selenium.interactions.Actions;

import org.zkoss.zktest.zats.WebDriverTestCase;

public class B50_ZK_442Test extends WebDriverTestCase {
	@Test
	public void test() {
		Actions act = new Actions(connect());
		click(jq("@menu"));
		waitResponse();
		act.keyDown(Keys.SHIFT).sendKeys(Keys.TAB).keyUp(Keys.SHIFT).build().perform();
		waitResponse();
		Assert.assertTrue(isZKLogAvailable());
	}
}
