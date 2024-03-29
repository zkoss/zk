/* B95_ZK_4774Test.java

	Purpose:
		
	Description:
		
	History:
		Wed Jan 20 14:50:59 CST 2021, Created by rudyhuang

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;

/**
 * @author rudyhuang
 */
public class B95_ZK_4774Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();

		click(jq("@button:eq(0)"));
		click(jq("@button:eq(1)"));
		waitResponse();
		assertNoJSError();
		Assertions.assertFalse(hasError());
	}
}
