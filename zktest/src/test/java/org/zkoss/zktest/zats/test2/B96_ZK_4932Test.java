/* B96_ZK_4932Test.java

	Purpose:
		
	Description:
		
	History:
		Fri Jun 18 11:53:46 CST 2021, Created by rudyhuang

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;

/**
 * @author rudyhuang
 */
public class B96_ZK_4932Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();

		click(jq("@datebox"));
		waitResponse();
		click(jq("@button"));
		waitResponse();

		Assertions.assertFalse(isZKLogAvailable());
	}
}
