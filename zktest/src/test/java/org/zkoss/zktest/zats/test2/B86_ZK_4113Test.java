/* B86_ZK_4113Test.java

		Purpose:
		
		Description:
		
		History:
				Thu Nov 22 15:35:46 CST 2018, Created by leon

Copyright (C) 2018 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;

public class B86_ZK_4113Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		Assertions.assertFalse(isZKLogAvailable());
		
		click(jq(".z-orgnode > i").eq(0));
		waitResponse();
		Assertions.assertFalse(isZKLogAvailable());
		
		click(jq(".z-orgnode > i").eq(0));
		waitResponse();
		Assertions.assertFalse(isZKLogAvailable());
	}
}
