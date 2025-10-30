/* B50_ZK_620aTest.java

		Purpose:
		
		Description:
		
		History:
				Mon Apr 22 16:16:41 CST 2019, Created by leon

Copyright (C) 2019 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;

public class B50_ZK_620aTest extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		waitResponse();
		Assertions.assertFalse(isZKLogAvailable());
		click(jq("@tab:contains(Discovery)"));
		waitResponse();
		String zklog = getZKLog();
		Assertions.assertTrue(zklog.contains("beforeSize"));
		Assertions.assertTrue(zklog.contains("onFitSize"));
		Assertions.assertTrue(zklog.contains("onSize"));
	}
}
