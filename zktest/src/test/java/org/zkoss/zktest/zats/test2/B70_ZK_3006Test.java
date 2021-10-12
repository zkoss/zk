/* B70_ZK_3006Test.java

	Purpose:
		
	Description:
		
	History:
		Wed Apr 10 10:38:41 CST 2019, Created by rudyhuang

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
public class B70_ZK_3006Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();

		click(jq("@chosenbox"));
		waitResponse();
		getActions().sendKeys(Keys.DOWN).perform();
		waitResponse();
		getActions().keyDown(Keys.SHIFT).sendKeys(";").keyUp(Keys.SHIFT).perform();
		Assert.assertNotEquals(":", jq("@chosenbox input").val());
		Assert.assertEquals(1, jq(".z-chosenbox-item").length());
	}
}
