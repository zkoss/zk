/* B96_ZK_4853Test.java

	Purpose:
		
	Description:
		
	History:
		Tue Apr 20 12:18:50 CST 2021, Created by rudyhuang

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.Assert;
import org.junit.Test;

import org.zkoss.zktest.zats.WebDriverTestCase;
import org.zkoss.zktest.zats.ztl.JQuery;

/**
 * @author rudyhuang
 */
public class B96_ZK_4853Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();

		click(jq("@button"));
		waitResponse();
		assertNoJSError();

		final JQuery treeBody = jq(widget("@tree").$n("body"));
		treeBody.scrollTop(treeBody.scrollHeight()); // to the bottom
		waitResponse();
		Assert.assertTrue("Checkbox invisible", jq("@checkbox").isVisible());

		click(jq("@checkbox"));
		waitResponse();
		Assert.assertTrue("Event is not sent", isZKLogAvailable());
	}
}
