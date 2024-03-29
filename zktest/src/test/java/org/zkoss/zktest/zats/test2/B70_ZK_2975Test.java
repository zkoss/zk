/* B70_ZK_2975Test.java

	Purpose:
		
	Description:
		
	History:
		Wed Apr 03 16:48:49 CST 2019, Created by rudyhuang

Copyright (C) 2019 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;

/**
 * @author rudyhuang
 */
public class B70_ZK_2975Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();

		type(widget("@datebox").$n("real"), "mars 2015");
		waitResponse();

		Assertions.assertFalse(hasError(), "Validation failed");
	}
}
