/* B36_2797773Test.java

	Purpose:
		
	Description:
		
	History:
		Wed Jun 19 10:28:02 CST 2019, Created by rudyhuang

Copyright (C) 2019 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;

/**
 * @author rudyhuang
 */
public class B36_2797773Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();

		click(jq("@intbox"));
		waitResponse();
		selectAll();
		cut();
		click(jq("body"));
		waitResponse();
		Assertions.assertEquals("", jq("@intbox").val());

		click(jq("@intbox"));
		waitResponse();
		paste();
		waitResponse();
		Assertions.assertEquals("1234567", jq("@intbox").val());
	}
}
