/* B95_ZK_4583Test.java

	Purpose:
		
	Description:
		
	History:
		Thu Jun 11 18:57:44 CST 2020, Created by rudyhuang

Copyright (C) 2020 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.Assert;
import org.junit.Test;

import org.zkoss.zktest.zats.WebDriverTestCase;

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
		Assert.assertEquals("Height should be the same", listboxHeight1, jq("@listbox").height(), 1);
		Assert.assertEquals("Height should be the same", gridHeight1, jq("@grid").height(), 1);
	}
}
