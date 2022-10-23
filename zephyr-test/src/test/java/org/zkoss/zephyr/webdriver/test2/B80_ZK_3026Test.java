/* B80_ZK_3026Test.java
	Purpose:

	Description:

	History:
		Mon Jan 25 15:36:18 CST 2016, Created by jameschu
Copyright (C) 2015 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zephyr.webdriver.test2;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import org.zkoss.zephyr.webdriver.ZephyrClientMVVMTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

/**
 * @author jameschu
 */
public class B80_ZK_3026Test extends ZephyrClientMVVMTestCase {

	@Test
	public void test() {
		connect();
		JQuery btns = jq("@button");
		JQuery btn1 = btns.eq(0);
		JQuery btn2 = btns.eq(1);
		checkResult("ABCDE");
		click(btn1);
		waitResponse();
		checkResult("AABCDE");
		click(btn2);
		waitResponse();
		checkResult("ABCDE");
		click(btn1);
		waitResponse();
		checkResult("AABCDE");
		click(btn2);
		waitResponse();
		checkResult("ABCDE");
		click(btn1);
		waitResponse();
		checkResult("AABCDE");
		click(btn2);
		waitResponse();
		checkResult("ABCDE");
	}

	public void checkResult(String expectedStr) {
		String result = "";
		JQuery labels = jq("@div @label");
		for (JQuery l : labels) {
			result += l.text();
		}
		assertEquals(expectedStr, result);
	}
}
