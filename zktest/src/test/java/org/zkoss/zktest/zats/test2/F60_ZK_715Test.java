/* F60_ZK_715Test.java

	Purpose:
		
	Description:
		
	History:
		Mon Apr 15 12:29:57 CST 2019, Created by rudyhuang

Copyright (C) 2019 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.Keys;
import org.openqa.selenium.interactions.Actions;

import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

/**
 * @author rudyhuang
 */
public class F60_ZK_715Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();

		testPaging(jq("$listbox1"));
		testPaging(jq("$listbox3"));
		testPaging(jq("$listbox5"));
		testPaging(jq("$listbox6"));
	}

	private void testPaging(JQuery lb) {
		click(lb.find("@listitem:contains(Item 11)"));
		waitResponse();

		Actions actions = getActions();
		actions.sendKeys(Keys.DOWN).perform();
		waitResponse();
		Assertions.assertEquals("2", lb.find("@paging .z-paging-input").val());

		actions.sendKeys(Keys.UP).perform();
		waitResponse();
		Assertions.assertEquals("1", lb.find("@paging .z-paging-input").val());

		actions.sendKeys(Keys.PAGE_DOWN).perform();
		waitResponse();
		Assertions.assertEquals("2", lb.find("@paging .z-paging-input").val());

		actions.sendKeys(Keys.PAGE_UP).perform();
		waitResponse();
		Assertions.assertEquals("1", lb.find("@paging .z-paging-input").val());
	}
}
