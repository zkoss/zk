/* F85_ZK_3507Test.java

		Purpose:
                
		Description:
                
		History:
				Fri Mar 29 09:55:37 CST 2019, Created by charlesqiu

Copyright (C) 2019 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.Keys;
import org.openqa.selenium.interactions.Actions;

import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

public class F85_ZK_3507Test extends WebDriverTestCase {

	@Test
	public void test() {
		connect();

		verify(jq(".z-listitem"), jq(".z-listitem-selected"));
		verify(jq(".z-treerow"), jq(".z-treerow-selected"));
	}

	private void verify(JQuery item, JQuery selectedItems) {
		click(item);
		waitResponse();

		sendCombinedKey(Keys.DOWN);
		Assertions.assertEquals(2, selectedItems.length());

		sendCombinedKey(Keys.UP);
		Assertions.assertEquals(1, selectedItems.length());

		sendCombinedKey(Keys.END);
		Assertions.assertEquals(7, selectedItems.length());

		sendCombinedKey(Keys.HOME);
		Assertions.assertEquals(1, selectedItems.length());
	}

	private void sendCombinedKey(Keys key) {
		new Actions(driver).keyDown(Keys.SHIFT).sendKeys(key).keyUp(Keys.SHIFT).build().perform();
		waitResponse();
	}
}
