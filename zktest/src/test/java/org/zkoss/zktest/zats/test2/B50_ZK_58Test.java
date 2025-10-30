/* B50_ZK_58Test.java

		Purpose:
		
		Description:
		
		History:
				Tue Apr 02 17:47:24 CST 2019, Created by leon

Copyright (C) 2019 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;

public class B50_ZK_58Test extends WebDriverTestCase {
	@Test
	public void test() {
		try {
			connect();
			click(jq(".z-button"));
			waitResponse();
		} catch (Exception e) {
			Assertions.fail();
		}
	}
}
