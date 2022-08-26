/* F85_ZK_3636Test.java

		Purpose:
                
		Description:
                
		History:
				Fri Mar 29 09:41:12 CST 2019, Created by charlesqiu

Copyright (C) 2019 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

public class F85_ZK_3636Test extends WebDriverTestCase {

	@Test
	public void test() {
		connect();

		JQuery stack = jq(".z-button .z-icon-stack");
		Assertions.assertTrue(stack.find(".z-icon-home").exists());
		Assertions.assertTrue(stack.find(".z-icon-check").exists());
	}
}
