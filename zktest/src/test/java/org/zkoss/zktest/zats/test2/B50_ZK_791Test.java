/* B50_ZK_791Test.java

		Purpose:
		
		Description:
		
		History:
				Fri Apr 26 12:34:05 CST 2019, Created by leon

Copyright (C) 2019 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.Keys;
import org.openqa.selenium.interactions.Actions;

import org.zkoss.test.webdriver.WebDriverTestCase;

public class B50_ZK_791Test extends WebDriverTestCase {
	@Test
	public void test() {
		Actions act = new Actions(connect());
		click(jq("@listcell:contains(options 0)"));
		waitResponse();
		click(jq("@button:contains(OK)"));
		waitResponse();
		act.keyDown(Keys.SHIFT).perform();
		waitResponse();
		click(jq("@listcell:contains(options 7)"));
		waitResponse();
		act.keyUp(Keys.SHIFT).perform();
		waitResponse();
		Assertions.assertEquals("list size = 8", jq(".z-messagebox").text().trim());
	}
}
