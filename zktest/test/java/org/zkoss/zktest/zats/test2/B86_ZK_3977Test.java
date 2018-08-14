/* B86_ZK_3977Test.java

        Purpose:
                
        Description:
                
        History:
                Mon Aug 13 10:11:21 CST 2018, Created by charlesqiu

Copyright (C) 2018 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.Assert;
import org.junit.Test;

import org.zkoss.zktest.zats.WebDriverTestCase;
import org.zkoss.zktest.zats.ztl.JQuery;

public class B86_ZK_3977Test extends WebDriverTestCase {

	@Test
	public void test() {
		connect();
		JQuery toggle = jq(".z-button:contains(Toggle)");
		JQuery showListenersSize = jq(".z-button:contains(ShowListenersSize)");

		click(toggle);
		waitResponse();
		click(showListenersSize);
		waitResponse();
		String listenersSize = getZKLog();
		closeZKLog();
		waitResponse();

		for (int i = 0; i < 2; i++) {
			click(toggle);
			waitResponse();
		}
		click(showListenersSize);
		waitResponse();
		Assert.assertEquals(listenersSize, getZKLog());
	}
}