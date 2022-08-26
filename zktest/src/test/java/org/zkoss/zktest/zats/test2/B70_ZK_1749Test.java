/* B70_ZK_1749Test.java

	Purpose:
		
	Description:
		
	History:
		Mon Jul 01 15:08:16 CST 2019, Created by rudyhuang

Copyright (C) 2019 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

/**
 * @author rudyhuang
 */
public class B70_ZK_1749Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();

		JQuery tbInput1 = jq("@timebox .z-timebox-input");
		click(tbInput1);
		setCursorPosition(tbInput1, 0);
		sendKeys(tbInput1, "121212\t");

		click(tbInput1);
		selectAll();
		copy();

		JQuery tbInput2 = jq("@timebox:last .z-timebox-input");
		click(tbInput2);
		selectAll();
		paste();

		click(jq("@button"));
		waitResponse();

		Assertions.assertNotEquals("", tbInput2.val());
		Assertions.assertEquals(tbInput1.val(), tbInput2.val());
	}
}
