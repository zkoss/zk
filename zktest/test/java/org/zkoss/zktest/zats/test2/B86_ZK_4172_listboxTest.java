/* B86_ZK_4172_listboxTest.java

	Purpose:
		
	Description:
		
	History:
		Wed Jan 16 11:05:27 CST 2019, Created by rudyhuang

Copyright (C) 2019 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.Assert;
import org.junit.Test;

import org.zkoss.zktest.zats.WebDriverTestCase;

/**
 * @author rudyhuang
 */
public class B86_ZK_4172_listboxTest extends WebDriverTestCase {
	@Test
	public void testMethod1() {
		connect();
		sleep(500);

		int scrollHeight = jq("@listbox > .z-listbox-body").scrollHeight();
		jq("@listbox > .z-listbox-body").scrollTop(scrollHeight / 3 * 2); // approximately 130
		waitResponse();

		int tPadHeight = getTopPadHeight();
		click(jq("@button:eq(0)"));
		waitResponse();
		Assert.assertEquals("The height of topPad has been changed.", tPadHeight, getTopPadHeight());
	}

	@Test
	public void testMethod2() {
		connect();
		sleep(500);

		int scrollHeight = jq("@listbox > .z-listbox-body").scrollHeight();
		jq("@listbox > .z-listbox-body").scrollTop(scrollHeight / 3 * 2); // approximately 130
		waitResponse();

		int tPadHeight = getTopPadHeight();
		click(jq("@button:eq(1)"));
		waitResponse();
		Assert.assertEquals("The height of topPad has been changed.", tPadHeight, getTopPadHeight());
	}

	@Test
	public void testMethod3() {
		connect();
		sleep(500);

		int scrollHeight = jq("@listbox > .z-listbox-body").scrollHeight();
		jq("@listbox > .z-listbox-body").scrollTop(scrollHeight / 3 * 2); // approximately 130
		waitResponse();

		int tPadHeight = getTopPadHeight();
		click(jq("@button:eq(2)"));
		waitResponse();
		Assert.assertEquals("The height of topPad has been changed.", tPadHeight, getTopPadHeight());
	}

	private int getTopPadHeight() {
		return Integer.parseInt(widget("@listbox").$n("tpad").get("clientHeight"));
	}
}
