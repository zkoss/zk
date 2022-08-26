/* B85_ZK_3330Test.java

	Purpose:
		
	Description:
		
	History:
		Fri Jan 05 15:38:26 CST 2018, Created by rudyhuang

Copyright (C) 2018 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;

/**
 * @author rudyhuang
 */
public class B85_ZK_3330Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();

		click(jq("@button:eq(1)"));
		waitResponse(true);

		int viewportHeight = jq("@tree").height();
		int itemTop = Integer.parseInt(zk("$ti32").eval("offsetTop()"));
		int scrollTop = jq("@tree .z-tree-body").scrollTop();
		Assertions.assertTrue(scrollTop > 0,
				"The scroll is still at the top position");
		assertInTheViewport(itemTop - scrollTop, 0, viewportHeight);
	}

	private void assertInTheViewport(int value, int smaller, int bigger) {
		Assertions.assertTrue(value >= smaller,
				String.format("The value(%d) is smaller than %d", value, smaller));
		Assertions.assertTrue(value <= bigger,
				String.format("The value(%d) is bigger than %d", value, bigger));
	}
}
