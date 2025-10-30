/* B50_ZK_699_ListgroupTest.java

		Purpose:
		
		Description:
		
		History:
				Tue Apr 23 17:38:19 CST 2019, Created by leon

Copyright (C) 2019 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;

public class B50_ZK_699_ListgroupTest extends WebDriverTestCase {
	@Test
	public void test() {
		try {
			connect();
			click(jq(".z-listgroup-icon").eq(0));
			waitResponse();
			click(jq("@button"));
			waitResponse();
			click(jq(".z-listgroup-icon").eq(0));
			waitResponse();
			Assertions.assertFalse(isZKLogAvailable());
		} catch (Exception e) {
			Assertions.fail();
		}
	}
}

