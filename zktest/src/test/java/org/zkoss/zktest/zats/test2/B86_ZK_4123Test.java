/* B86_ZK_4123Test.java

		Purpose:
		
		Description:
		
		History:
				Mon Dec 03 14:38:45 CST 2018, Created by leon

Copyright (C) 2018 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;

public class B86_ZK_4123Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		click(jq("$btn1"));
		waitResponse();
		Assertions.assertEquals("true", getZKLog());
	}
}
