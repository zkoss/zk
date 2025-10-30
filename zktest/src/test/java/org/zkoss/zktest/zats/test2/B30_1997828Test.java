/* B30_1997828Test.java

	Purpose:
		
	Description:
		
	History:
		Tue Sep 22 11:58:05 CST 2020, Created by rudyhuang

Copyright (C) 2020 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

/**
 * @author rudyhuang
 */
public class B30_1997828Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();

		click(jq("img"));
		waitResponse();

		JQuery any = jq("$any");
		Assertions.assertTrue(any.exists());
		Assertions.assertTrue(any.isVisible());
	}
}
