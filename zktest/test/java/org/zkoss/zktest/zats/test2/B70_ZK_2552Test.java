/* B70_ZK_2552Test.java

	Purpose:
		
	Description:
		
	History:
		Wed Apr 03 11:08:26 CST 2019, Created by rudyhuang

Copyright (C) 2019 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.Assert;
import org.junit.Test;

import org.zkoss.zktest.zats.WebDriverTestCase;

/**
 * @author rudyhuang
 */
public class B70_ZK_2552Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();

		click(widget("@tab:eq(2)").$n("cls"));
		waitResponse();

		Assert.assertEquals("eee", jq("@tab:last").text());
	}
}
