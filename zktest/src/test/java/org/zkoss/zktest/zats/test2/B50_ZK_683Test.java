/* B50_ZK_683Test.java

		Purpose:
		
		Description:
		
		History:
				Tue Apr 23 17:02:29 CST 2019, Created by leon

Copyright (C) 2019 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.Keys;
import org.openqa.selenium.interactions.Actions;

import org.zkoss.test.webdriver.WebDriverTestCase;

public class B50_ZK_683Test extends WebDriverTestCase {
	@Test
	public void test() {
		Actions act = new Actions(connect());
		click(jq("@intbox"));
		waitResponse();
		for (int i = 0; i < 4; i++) {
			act.sendKeys(Keys.TAB).perform();
			waitResponse();
		}
		Assertions.assertEquals(4, jq(".z-errorbox").length());
	}
}
