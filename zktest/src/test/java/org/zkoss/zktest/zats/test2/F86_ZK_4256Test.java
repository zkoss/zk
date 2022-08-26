/* F86_ZK_4256Test.java

	Purpose:
		
	Description:
		
	History:
		Thu Aug 15 16:52:04 CST 2019, Created by rudyhuang

Copyright (C) 2019 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;

/**
 * @author rudyhuang
 */
public class F86_ZK_4256Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();

		click(jq("@button:contains(Start)"));
		waitResponse();
		click(jq("@button:contains(Abort)"));
		waitResponse();

		final String currentValue = jq("$time").text();
		sleep(2000);
		Assertions.assertNotEquals(currentValue, jq("$time").text());
	}
}
