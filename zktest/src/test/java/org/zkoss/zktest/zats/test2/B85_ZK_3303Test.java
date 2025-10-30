/* B85_ZK_3303Test.java

	Purpose:
		
	Description:
		
	History:
		Tue Jul 11 12:25:48 CST 2017, Created by rudyhuang

Copyright (C) 2017 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;

/**
 * @author rudyhuang
 */
public class B85_ZK_3303Test extends WebDriverTestCase {
	@Test
	public void test() throws Exception {
		connect();

		click(jq("@tree .z-treerow:first"));
		waitResponse();

		click(jq("@button"));
		waitResponse();

		Assertions.assertFalse(
				jq("@tree .z-treerow:first").hasClass("z-treerow-selected"),
				"Root item shouldn't be selected."
				);
		Assertions.assertTrue(
				jq("@tree .z-treerow:eq(1)").hasClass("z-treerow-selected"),
				"First child item should be selected."
				);
	}
}
