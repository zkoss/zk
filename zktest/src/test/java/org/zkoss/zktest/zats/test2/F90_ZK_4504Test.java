/* F90_ZK_4504Test.java

	Purpose:
		
	Description:
		
	History:
		Wed Feb 12 17:55:33 CST 2020, Created by rudyhuang

Copyright (C) 2020 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.Widget;

/**
 * @author rudyhuang
 */
public class F90_ZK_4504Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();

		click(jq("@button:eq(0)"));
		waitResponse();
		Widget win1 = widget("@window");
		Assertions.assertEquals("Set 1", win1.get("title"));
		Assertions.assertEquals("500px", win1.get("width"));
		Assertions.assertEquals("400px", win1.get("height"));
		Assertions.assertEquals("border: 1px solid red", win1.get("style"));

		click(jq("@button:eq(1)"));
		waitResponse();
		Assertions.assertEquals("Set 2", win1.get("title"));
		Assertions.assertEquals("800px", win1.get("width"));
		Assertions.assertEquals("300px", win1.get("height"));
		Assertions.assertEquals("border: 1px dotted green", win1.get("style"));
	}
}
