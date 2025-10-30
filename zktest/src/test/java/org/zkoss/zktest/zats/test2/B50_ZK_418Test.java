/* B50_ZK_418Test.java

		Purpose:
		
		Description:
		
		History:
				Tue Apr 09 14:35:09 CST 2019, Created by leon

Copyright (C) 2019 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.Keys;
import org.openqa.selenium.interactions.Actions;

import org.zkoss.test.webdriver.WebDriverTestCase;

public class B50_ZK_418Test extends WebDriverTestCase {
	@Test
	public void test() {
		Actions act = new Actions(connect());
		click(jq("@listcell:contains(Option 2)"));
		waitResponse();
		act.keyDown(Keys.SHIFT).build().perform();
		click(jq("@listcell:contains(Option 6)"));
		waitResponse();
		act.keyUp(Keys.SHIFT).build().perform();
		click(jq("@button:contains(t)"));
		waitResponse();
		Assertions.assertEquals(5, jq(".z-messagebox").text().split(", ").length);
		for (int i = 0; i < 5; i++) {
			Assertions.assertTrue(jq(".z-messagebox").text().split(", ")[i].contains(jq(".z-listitem-selected").eq(i).attr("id")));
		}
		click(jq("@button:contains(OK)"));
		waitResponse();
	}
}
