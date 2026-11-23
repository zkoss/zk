/* B95_ZK_4646Test.java

	Purpose:
		
	Description:
		
	History:
		Thu Aug 27 17:22:33 CST 2020, Created by rudyhuang

Copyright (C) 2020 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.Assert;
import org.junit.Test;

import org.zkoss.zktest.zats.WebDriverTestCase;

/**
 * @author rudyhuang
 */
public class B95_ZK_4646Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();

		click(widget("@treeitem").$n("open"));
		waitResponse();
		assertNoJSError();
		Assert.assertEquals(0, jq("@treeitem:contains(invisible)").length());
	}
}
