/* B80_ZK_2874Test.java

	Purpose:
		
	Description:
		
	History:
		Mon Apr 08 14:52:23 CST 2019, Created by rudyhuang

Copyright (C) 2019 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;

/**
 * @author rudyhuang
 */
public class B80_ZK_2874Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();

		waitResponse();
		Assertions.assertEquals("page 2", jq("$inc @label").text());
	}
}
