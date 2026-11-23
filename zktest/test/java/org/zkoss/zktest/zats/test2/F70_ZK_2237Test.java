/* F70_ZK_2237Test.java

	Purpose:
		
	Description:
		
	History:
		Wed Apr 17 16:49:59 CST 2019, Created by rudyhuang

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
public class F70_ZK_2237Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();

		JQuery ok = jq("@a:first");
		click(ok);
		Assert.assertTrue(ok.is("[disabled]"));
		sleep(1000);
		Assert.assertFalse(ok.is("[disabled]"));

		JQuery ok2 = jq("@a:contains(OK 2)");
		JQuery cancel = jq("@a:contains(Cancel)");
		click(ok2);
		Assert.assertTrue(ok2.is("[disabled]"));
		Assert.assertTrue(cancel.is("[disabled]"));
		sleep(1000);
		Assert.assertFalse(ok2.is("[disabled]"));
		Assert.assertFalse(cancel.is("[disabled]"));

		click(cancel);
		Assert.assertTrue(ok2.is("[disabled]"));
		Assert.assertTrue(cancel.is("[disabled]"));
		sleep(1000);
		Assert.assertFalse(ok2.is("[disabled]"));
		Assert.assertFalse(cancel.is("[disabled]"));

		click(jq("@button:eq(0)"));
		waitResponse();
		Assert.assertTrue(ok2.is("[disabled]"));

		click(jq("@button:eq(1)"));
		waitResponse();
		Assert.assertFalse(ok2.is("[disabled]"));
	}
}
