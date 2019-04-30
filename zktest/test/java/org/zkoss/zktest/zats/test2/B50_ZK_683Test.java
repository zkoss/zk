/* B50_ZK_683Test.java

		Purpose:
		
		Description:
		
		History:
				Tue Apr 23 17:02:29 CST 2019, Created by leon

Copyright (C) 2019 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.Keys;
import org.openqa.selenium.interactions.Actions;
import org.zkoss.zktest.zats.WebDriverTestCase;

public class B50_ZK_683Test extends WebDriverTestCase {
	@Test
	public void test() {
		Actions act = new Actions(connect());
		act.sendKeys(Keys.TAB).sendKeys(Keys.TAB).sendKeys(Keys.TAB).sendKeys(Keys.TAB).perform();
		waitResponse();
		Assert.assertEquals(4, jq(".z-errorbox").length());
	}
}
