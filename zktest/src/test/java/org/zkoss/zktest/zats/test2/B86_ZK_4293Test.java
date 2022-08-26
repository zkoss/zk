/* B86_ZK_4293Test.java

		Purpose:
		
		Description:
		
		History:
				Fri Jan 17 16:45:51 CST 2020, Created by leon

Copyright (C) 2020 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;

public class B86_ZK_4293Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		click(jq("@button").eq(0));
		waitResponse();
		click(jq("@button").eq(1));
		waitResponse(true);
		click(jq("@textbox"));
		waitResponse();
		click(jq("@label"));
		waitResponse();
		Assertions.assertFalse(isZKLogAvailable());
	}
}
