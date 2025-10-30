/* B95_ZK_4517_AUTest.java

	Purpose:
		
	Description:
		
	History:
		Fri Jan 08 12:50:34 CST 2021, Created by rudyhuang

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;

/**
 * @author rudyhuang
 */
public class B95_ZK_4517_AUTest extends WebDriverTestCase {
	@Test
	public void test() {
		connect();

		click(jq("@button"));
		waitResponse();
		Assertions.assertNotEquals(0, jq("@listitem").length());
	}
}
