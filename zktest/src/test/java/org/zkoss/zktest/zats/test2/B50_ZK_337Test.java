/* B50_ZK_337Test.java

		Purpose:
		
		Description:
		
		History:
				Tue Apr 09 10:17:33 CST 2019, Created by leon

Copyright (C) 2019 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;

public class B50_ZK_337Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		Assertions.assertTrue(jq(".z-button[disabled]").css("cursor") != "pointer");
	}
}
