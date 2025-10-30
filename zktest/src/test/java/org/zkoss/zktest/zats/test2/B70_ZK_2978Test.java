/* B70_ZK_2978Test.java

	Purpose:
		
	Description:
		
	History:
		Wed Apr 03 16:36:58 CST 2019, Created by rudyhuang

Copyright (C) 2019 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;

/**
 * @author rudyhuang
 */
public class B70_ZK_2978Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();

		click(jq("@groupbox:eq(0) @caption"));
		waitResponse(true);

		click(jq("@groupbox:eq(1) @caption"));
		waitResponse(true);

		Assertions.assertTrue(jq("@west .z-scrollbar-vertical-embed").exists());
	}
}
