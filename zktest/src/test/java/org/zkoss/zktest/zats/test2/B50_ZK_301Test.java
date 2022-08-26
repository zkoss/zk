/* B50_ZK_301Test.java

		Purpose:
		
		Description:
		
		History:
				Mon Apr 08 17:08:46 CST 2019, Created by leon

Copyright (C) 2019 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;

public class B50_ZK_301Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		for(int i = 0; i < 3; i++) {
			click(jq(".z-icon-angle-double-right"));
			waitResponse();
			click(jq(".z-icon-angle-double-left"));
			waitResponse();
		}
		Assertions.assertFalse(isZKLogAvailable(), "should have no error");
	}
}
