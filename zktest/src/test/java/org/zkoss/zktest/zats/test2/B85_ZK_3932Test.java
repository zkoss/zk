/* B85_ZK_3932Test.java

		Purpose:
		
		Description:
		
		History:
				Thu Jun 13 12:36:59 CST 2019, Created by leon

Copyright (C) 2019 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;

public class B85_ZK_3932Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		click(jq("@button").eq(0));
		waitResponse();
		click(jq("@button").eq(1));
		waitResponse();
		Assertions.assertFalse(isZKLogAvailable());
	}
}
