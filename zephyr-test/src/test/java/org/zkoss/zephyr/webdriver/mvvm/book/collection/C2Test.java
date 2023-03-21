/* C2Test.java

	Purpose:
		
	Description:
		
	History:
		Mon Apr 26 11:52:03 CST 2021, Created by rudyhuang

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zephyr.webdriver.mvvm.book.collection;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;

/**
 * @author rudyhuang
 */
public class C2Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		waitResponse();
		for (int i = 4; i >= 0; i--) {
			click(jq("@button"));
			waitResponse();
			assertEquals(i, jq("@listitem").length());
		}
	}
}
