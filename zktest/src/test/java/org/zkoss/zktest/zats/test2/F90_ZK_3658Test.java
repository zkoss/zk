/* F90_ZK_3658Test.java

	Purpose:
		
	Description:
		
	History:
		Tue Aug 20 11:21:58 CST 2019, Created by rudyhuang

Copyright (C) 2019 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;

/**
 * @author rudyhuang
 */
public class F90_ZK_3658Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();

		type(jq("input[type=\"text\"]"), "test");
		click(jq("@button"));
		waitResponse();

		Assertions.assertEquals("test", jq("input[type=\"text\"]").val());
	}
}
