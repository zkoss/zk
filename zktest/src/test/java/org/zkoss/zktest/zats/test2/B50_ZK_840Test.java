/* B50_ZK_840Test.java

		Purpose:
		
		Description:
		
		History:
				Thu May 23 15:12:27 CST 2019, Created by leon

Copyright (C) 2019 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;

public class B50_ZK_840Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		waitResponse();
		click(jq(".z-tree-icon"));
		waitResponse();
		Assertions.assertFalse(isZKLogAvailable());
	}
}
