/* CommandTest.java
	Purpose:

	Description:

	History:
		Thu May 06 17:14:31 CST 2021, Created by jameschu

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zephyr.webdriver.mvvm.book.databinding.commandbinding;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

/**
 * @author jameschu
 */
public class CommandTest extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		String[] blockIds = { "d1", "d2" };
		String[] resultTexts = { "newOrder", "saveOrder", "defaultAction", null, null };
		for (int i = 0; i < blockIds.length; i++) {
			JQuery btns = jq("$" + blockIds[i] + " @button");
			JQuery menuitems = jq("$" + blockIds[i] + " @menuitem");
			String[] zkLog = getZKLog().split("\n");
			for (int j = 0; j < resultTexts.length; j++) {
				click(btns.eq(j));
				click(menuitems.eq(j));
				waitResponse();
				String resultText = resultTexts[j];
				if (resultText != null) {
					zkLog = getZKLog().split("\n");
					assertEquals(resultText, zkLog[zkLog.length - 1]);
					assertEquals(resultText, zkLog[zkLog.length - 2]);
				} else {
					assertEquals(zkLog.length, getZKLog().split("\n").length);
				}
			}
		}
	}
}
