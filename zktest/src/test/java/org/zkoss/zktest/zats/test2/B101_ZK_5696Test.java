/* B101_ZK_5696Test.java

	Purpose:

	Description:

	History:
		11:58 AM 2024/9/9, Created by jumperchen

Copyright (C) 2024 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;

/**
 * @author jumperchen
 */
public class B101_ZK_5696Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		for (int i = 0; i < 5; ) {
			click(jq(".z-button"));
			waitResponse();
			assertEquals(1, jq(".z-label:contains(" + ++i + ")").length());
			assertEquals(i,  jq(".z-label:contains(conditional)").length());
		}
	}
}
