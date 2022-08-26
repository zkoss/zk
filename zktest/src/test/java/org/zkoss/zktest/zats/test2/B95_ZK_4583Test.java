/* B95_ZK_4583Test.java

	Purpose:
		
	Description:
		
	History:
		Thu Jun 11 18:57:44 CST 2020, Created by rudyhuang

Copyright (C) 2020 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;

/**
 * @author rudyhuang
 */
public class B95_ZK_4583Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();

		click(jq("@button:eq(0)"));
		waitResponse();
		int listboxHeight1 = jq("@listbox").height();
		int gridHeight1 = jq("@grid").height();

		click(jq("@button:eq(1)"));
		waitResponse();
		Assertions.assertEquals(listboxHeight1, jq("@listbox").height(), 1, "Height should be the same");
		Assertions.assertEquals(gridHeight1, jq("@grid").height(), 1, "Height should be the same");
	}
}
