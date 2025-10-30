/* B96_ZK_4842Test.java

	Purpose:
		
	Description:
		
	History:
		Tue Apr 20 18:21:10 CST 2021, Created by rudyhuang

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;

/**
 * @author rudyhuang
 */
public class B96_ZK_4842Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();

		click(jq("@button:eq(0)"));
		waitResponse();
		Assertions.assertFalse(hasError());

		click(jq("@button:eq(1)"));
		waitResponse();
		Assertions.assertFalse(hasError());

		click(jq("@button:eq(2)"));
		waitResponse();
		Assertions.assertFalse(hasError());
	}
}
