/* B90_ZK_4464Test.java

	Purpose:
		
	Description:
		
	History:
		Thu Mar 12 15:52:48 CST 2020, Created by rudyhuang

Copyright (C) 2020 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;

/**
 * @author rudyhuang
 */
public class B90_ZK_4464Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();

		sleep(10000);
		click(jq("@button"));
		waitResponse();
		Assertions.assertEquals("retry\nretry\nretry\nstopped", getZKLog());
	}
}
