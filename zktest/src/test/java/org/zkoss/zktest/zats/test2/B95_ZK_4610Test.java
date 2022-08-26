/* B95_ZK_4632Test.java

	Purpose:
		
	Description:
		
	History:
		Wed Aug 12 16:09:45 CST 2020, Created by rudyhuang

Copyright (C) 2020 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;

/**
 * @author rudyhuang
 */
public class B95_ZK_4610Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();

		click(jq("@button"));
		waitResponse();
		click(jq("@button"));
		waitResponse();
		Assertions.assertFalse(hasError());
		assertNoJSError();

		click(jq(".z-icon-bars"));
		waitResponse();
		Assertions.assertTrue(jq("@menupopup").isVisible());
	}
}
