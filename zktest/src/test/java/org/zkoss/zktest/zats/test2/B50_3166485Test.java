/* B50_3166485Test.java

		Purpose:
                
		Description:
                
		History:
				Thu Mar 21 15:39:17 CST 2019, Created by charlesqiu

Copyright (C) 2019 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;

public class B50_3166485Test extends WebDriverTestCase {

	@Test
	public void test() {
		connect();

		for (int i = 0; i < 5; i++) {
			click(jq(".z-button:eq(0)"));
			waitResponse();
		}

		click(jq(".z-button:eq(1)"));
		waitResponse();
		Assertions.assertEquals(getZKLog(), "false");
	}
}
