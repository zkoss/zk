/* F50_2839350Test.java

	Purpose:
		
	Description:
		
	History:
		Mon Apr 08 16:05:04 CST 2019, Created by rudyhuang

Copyright (C) 2019 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;

/**
 * @author rudyhuang
 */
public class F50_2839350Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();

		click(widget("@timebox").$n("btn-down"));
		waitResponse();

		Assertions.assertEquals("PM 12:00:00", jq("@timebox input").val());
	}
}
