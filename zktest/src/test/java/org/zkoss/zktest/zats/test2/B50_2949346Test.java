/* B50_2949346Test.java

	Purpose:
		
	Description:
		
	History:
		Thu Mar 21 12:20:42 CST 2019, Created by rudyhuang

Copyright (C) 2019 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;

/**
 * @author rudyhuang
 */
public class B50_2949346Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();

		click(jq("@button:eq(0)"));
		waitResponse();
		Assertions.assertEquals(4, jq("@treeitem").length());

		click(jq("@button:eq(1)"));
		waitResponse();
		Assertions.assertEquals(0, jq("@treeitem").length());
	}

	@Test
	public void test2() {
		connect();

		click(jq("@button:eq(2)"));
		waitResponse();
		Assertions.assertEquals(0, jq("@treeitem").length());
	}
}
