/* F85_ZK_3507Test.java

		Purpose:
                
		Description:
                
		History:
				Fri Mar 29 09:55:37 CST 2019, Created by charlesqiu

Copyright (C) 2019 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.Keys;
import org.openqa.selenium.interactions.Actions;

import org.zkoss.zktest.zats.WebDriverTestCase;
import org.zkoss.zktest.zats.ztl.JQuery;

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
		Assert.assertEquals(2, selectedItems.length());

		sendCombinedKey(Keys.UP);
		Assert.assertEquals(1, selectedItems.length());

		sendCombinedKey(Keys.END);
		Assert.assertEquals(7, selectedItems.length());

		sendCombinedKey(Keys.HOME);
		Assert.assertEquals(1, selectedItems.length());
	}

	private void sendCombinedKey(Keys key) {
		new Actions(driver).keyDown(Keys.SHIFT).sendKeys(key).build().perform();
		waitResponse();
	}
}
