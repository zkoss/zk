/* B80_ZK_3530Test.java

		Purpose:
                
		Description:
                
		History:
				Wed Mar 27 09:58:33 CST 2019, Created by charlesqiu

Copyright (C) 2019 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.Keys;

import org.zkoss.test.webdriver.WebDriverTestCase;

public class B80_ZK_3530Test extends WebDriverTestCase {

	@Test
	public void test() {
		connect();

		sendKeys(jq(".z-textbox"), Keys.ESCAPE);
		waitResponse();
		Assertions.assertFalse(isZKLogAvailable());
	}
}
