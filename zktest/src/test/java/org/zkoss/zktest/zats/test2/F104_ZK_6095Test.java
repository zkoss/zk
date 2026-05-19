/* F104_ZK_6095Test.java

		Purpose:

		Description:

		History:
				Wed May 13 17:10:17 CST 2026, Created by peakerlee

Copyright (C) 2026 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.Keys;
import org.openqa.selenium.interactions.Actions;

import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

public class F104_ZK_6095Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		// focus the textbox inside the center region so keydown originates
		// inside the borderlayout's widget subtree
		JQuery tb = jq("$tb");
		focus(tb);
		waitResponse();

		Actions action = getActions();
		action.keyDown(Keys.CONTROL).sendKeys("k").keyUp(Keys.CONTROL).perform();
		waitResponse();

		JQuery result = jq("$result");
		assertEquals("ctrlKey:75", result.text());

		action = getActions();
		action.keyDown(Keys.CONTROL).sendKeys("h").keyUp(Keys.CONTROL).perform();
		waitResponse();
		assertEquals("ctrlKey:72", result.text());
	}
}
