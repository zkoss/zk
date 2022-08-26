/* B50_ZK_773Test.java

		Purpose:
		
		Description:
		
		History:
				Fri Apr 26 12:15:44 CST 2019, Created by leon

Copyright (C) 2019 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;

public class B50_ZK_773Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		waitResponse();
		click(jq("@button"));
		waitResponse();
		Assertions.assertEquals("true", getZKLog());
	}
}
