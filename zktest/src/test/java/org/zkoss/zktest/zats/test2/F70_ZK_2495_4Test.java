/* F70_ZK_2495_4Test.java

	Purpose:
		
	Description:
		
	History:
		Wed Apr 17 17:09:31 CST 2019, Created by rudyhuang

Copyright (C) 2019 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;

/**
 * @author rudyhuang
 */
public class F70_ZK_2495_4Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();

		sleep(5000); // wait for crash
		Assertions.assertEquals("Ooooops!! ErrorCode: 4", jq("body").text());
	}
}
