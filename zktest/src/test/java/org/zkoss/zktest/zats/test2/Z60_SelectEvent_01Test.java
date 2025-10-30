/* Z60_SelectEvent_01Test.java

	Purpose:
		
	Description:
		
	History:
		Tue Apr 23 11:02:00 CST 2019, Created by rudyhuang

Copyright (C) 2019 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.hamcrest.MatcherAssert.assertThat;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.Keys;
import org.openqa.selenium.interactions.Actions;

import org.zkoss.test.webdriver.WebDriverTestCase;

/**
 * @author rudyhuang
 */
public class Z60_SelectEvent_01Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();

		selectComboitem(widget("@combobox"), 1);
		Assertions.assertEquals("[0B]", getZKLog());

		click(jq("@listitem:contains(0C)"));
		waitResponse();
		closeZKLog();
		new Actions(driver).keyDown(Keys.SHIFT)
				.click(toElement(jq("@listitem:contains(0F)")))
				.keyUp(Keys.SHIFT)
				.perform();
		waitResponse();
		String zkLog = getZKLog();
		assertThat(zkLog.substring(1, zkLog.length() - 1).split(", "), Matchers.arrayContainingInAnyOrder("0C", "0D", "0E", "0F"));

		closeZKLog();
		click(jq("@treerow:contains(0J)"));
		waitResponse();
		closeZKLog();
		click(jq("@treerow:contains(0F)"));
		waitResponse();
		zkLog = getZKLog();
		assertThat(zkLog.substring(1, zkLog.length() - 1).split(", "), Matchers.arrayContainingInAnyOrder("0J", "0F"));
	}
}
