/* F50_2994541Test.java

	Purpose:
		
	Description:
		
	History:
		Tue Apr 09 10:41:58 CST 2019, Created by rudyhuang

Copyright (C) 2019 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;

/**
 * @author rudyhuang
 */
public class F50_2994541Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();

		String color1 = jq("@label").css("backgroundColor");
		sleep(1000);
		Assertions.assertNotEquals(color1, jq("@label").css("backgroundColor"));
	}
}
