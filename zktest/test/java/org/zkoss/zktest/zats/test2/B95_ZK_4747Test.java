/* B95_ZK_4747Test.java

	Purpose:
		
	Description:
		
	History:
		Thu Dec 31 11:11:41 CST 2020, Created by rudyhuang

Copyright (C) 2020 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.Keys;

import org.zkoss.zktest.zats.WebDriverTestCase;

/**
 * @author rudyhuang
 */
public class B95_ZK_4747Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();

		click(jq("@textbox:first"));
		waitResponse();
		getActions().sendKeys(Keys.TAB).sendKeys(Keys.TAB).perform();
		waitResponse();
		Assert.assertEquals(widget(jq("@textbox:last")).uuid(), getEval("document.activeElement.id"));
	}
}
