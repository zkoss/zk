/* B101_ZK_5839Test.java

	Purpose:

	Description:

	History:
		5:21 PM 2024/11/7, Created by jumperchen

Copyright (C) 2024 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;

/**
 * @author jumperchen
 */
public class B101_ZK_5839Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		for (int i = 0; i < 5; i++) {
			click(jq(".z-button"));
			waitResponse();
			assertEquals("AAABBBCCC", jq(".z-div .z-div:eq("+i+")").text().replaceAll("[^A-Z]", ""));
		}
	}
}
