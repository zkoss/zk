/* F70_ZK_2531Test.java

	Purpose:
		
	Description:
		
	History:
		Wed Apr 17 17:31:49 CST 2019, Created by rudyhuang

Copyright (C) 2019 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;

/**
 * @author rudyhuang
 */
public class F70_ZK_2531Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		Assertions.assertEquals("1", jq("@checkbox input").attr("tabindex"));
		Assertions.assertEquals("2", jq("@tree .z-focus-a").attr("tabindex"));
		Assertions.assertEquals("3", jq("@button").attr("tabindex"));
		Assertions.assertEquals("4", jq("@listbox .z-focus-a").attr("tabindex"));
	}
}
