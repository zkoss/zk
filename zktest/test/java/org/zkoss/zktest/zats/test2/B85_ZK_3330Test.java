/* B85_ZK_3330Test.java

	Purpose:
		
	Description:
		
	History:
		Fri Jan 05 15:38:26 CST 2018, Created by rudyhuang

Copyright (C) 2018 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.Assert;
import org.junit.Test;

import org.zkoss.zktest.zats.WebDriverTestCase;

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
		Assert.assertTrue("The scroll is still at the top position", scrollTop > 0);
		assertInTheViewport(itemTop - scrollTop, 0, viewportHeight);
	}

	private void assertInTheViewport(int value, int smaller, int bigger) {
		Assert.assertTrue(String.format("The value(%d) is smaller than %d", value, smaller), value >= smaller);
		Assert.assertTrue(String.format("The value(%d) is bigger than %d", value, bigger), value <= bigger);
	}
}
