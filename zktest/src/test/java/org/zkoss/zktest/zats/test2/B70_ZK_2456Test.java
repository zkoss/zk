/* B70_ZK_2456Test.java

	Purpose:
		
	Description:
		
	History:
		Thu Mar 28 16:01:21 CST 2019, Created by rudyhuang

Copyright (C) 2019 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;

/**
 * @author rudyhuang
 */
public class B70_ZK_2456Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();

		click(jq("button:eq(0)"));
		click(jq("button:eq(1)"));
		click(jq("button:eq(2)"));
		waitResponse();

		Assertions.assertEquals("Selected: Demo\nSelected: Edit\nSelected: Add", getZKLog());
	}
}
