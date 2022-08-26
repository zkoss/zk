/* B50_ZK_925Test.java

		Purpose:
		
		Description:
		
		History:
				Fri May 24 12:15:08 CST 2019, Created by leon

Copyright (C) 2019 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.Keys;
import org.openqa.selenium.interactions.Actions;

import org.zkoss.test.webdriver.WebDriverTestCase;

public class B50_ZK_925Test extends WebDriverTestCase {
	@Test
	public void test() {
		Actions act = new Actions(connect());
		waitResponse();
		click(jq("@listbox:first @listcell").eq(0));
		waitResponse();
		act.keyDown(Keys.SHIFT).perform();
		waitResponse();
		click(jq("@listbox:first @listcell").eq(7));
		waitResponse();
		act.keyUp(Keys.SHIFT).perform();
		waitResponse();
		click(jq("@button").eq(0));
		waitResponse();
		Assertions.assertEquals("8", jq("@label").eq(1).text());
		
		click(jq("@listbox:last @listcell").eq(2));
		waitResponse();
		act.keyDown(Keys.SHIFT).perform();
		waitResponse();
		click(jq("@listbox:last @listcell").eq(6));
		waitResponse();
		act.keyUp(Keys.SHIFT).perform();
		waitResponse();
		click(jq("@button").eq(1));
		waitResponse();
		Assertions.assertEquals("5", jq("@label").eq(2).text());
	}
}
