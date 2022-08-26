/* B50_3310406Test.java

		Purpose:
                
		Description:
                
		History:
				Fri Mar 22 12:36:12 CST 2019, Created by charlesqiu

Copyright (C) 2019 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;

public class B50_3310406Test extends WebDriverTestCase {

	@Test
	public void test() {
		connect();

		click(jq(".z-button"));
		waitResponse();
		Assertions.assertEquals("size:0\nsize:0", getZKLog());
	}
}
