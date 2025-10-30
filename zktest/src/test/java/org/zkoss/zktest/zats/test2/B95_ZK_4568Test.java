/* B95_ZK_4568Test.java

	Purpose:
		
	Description:
		
	History:
		Mon Nov 02 11:51:40 CST 2020, Created by rudyhuang

Copyright (C) 2020 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;

/**
 * @author rudyhuang
 */
public class B95_ZK_4568Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		sleep(5000); // wait for crash
		Assertions.assertNotEquals("Ooooops!! ErrorCode: 5", jq("body").text());
	}
}
