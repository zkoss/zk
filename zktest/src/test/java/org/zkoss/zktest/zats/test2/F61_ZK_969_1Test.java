/* F61_ZK_969_1Test.java

	Purpose:
		
	Description:
		
	History:
		Tue Apr 16 17:16:47 CST 2019, Created by rudyhuang

Copyright (C) 2019 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;

/**
 * @author rudyhuang
 */
public class F61_ZK_969_1Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();

		click(jq("@button:eq(0)"));
		waitResponse();
		Assertions.assertEquals("Center", jq(".z-center-header").text());

		click(jq("@button:eq(1)"));
		waitResponse();
		Assertions.assertEquals("Center Label", jq(".z-center-body").text());

		click(jq("@button:eq(1)"));
		waitResponse();
		Assertions.assertTrue(hasError());
		click(jq(".z-messagebox-buttons .z-button"));
		waitResponse();

		click(jq("@button:eq(2)"));
		waitResponse();
		Assertions.assertEquals("CenterTest", jq(".z-center-header").text());
	}
}
