/* B70_ZK_2892Test.java

	Purpose:
		
	Description:
		
	History:
		Wed Apr 03 14:43:24 CST 2019, Created by rudyhuang

Copyright (C) 2019 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;

/**
 * @author rudyhuang
 */
public class B70_ZK_2892Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();

		int winLeft = jq("@window:eq(0)").offsetLeft();
		Assertions.assertNotEquals(winLeft, jq("@window:eq(1)").offsetLeft());
		Assertions.assertNotEquals(winLeft, jq("@window:eq(2)").offsetLeft());
	}
}
