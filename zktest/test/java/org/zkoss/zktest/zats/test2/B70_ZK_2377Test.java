/* B70_ZK_2377Test.java

	Purpose:
		
	Description:
		
	History:
		Thu Mar 28 15:47:15 CST 2019, Created by rudyhuang

Copyright (C) 2019 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.Assert;
import org.junit.Test;

import org.zkoss.zktest.zats.WebDriverTestCase;
import org.zkoss.zktest.zats.ztl.JQuery;

/**
 * @author rudyhuang
 */
public class B70_ZK_2377Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();

		JQuery body = jq("@listbox .z-listbox-body");
		body.scrollTop(body.scrollHeight());
		waitResponse();
		int bottomPx = body.scrollTop();

		body.scrollTop(0);
		waitResponse();
		body.scrollTop(bottomPx);
		waitResponse();

		Assert.assertEquals(bottomPx, body.scrollTop());
	}
}
