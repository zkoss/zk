/* B50_3165164Test.java

		Purpose:
                
		Description:
                
		History:
				Thu Mar 21 15:34:54 CST 2019, Created by charlesqiu

Copyright (C) 2019 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;

public class B50_3165164Test extends WebDriverTestCase {

	@Test
	public void test() {
		connect();

		click(jq(".z-tab-button"));
		waitResponse();
		Assertions.assertTrue(jq(".z-messagebox-window").exists());
	}
}
