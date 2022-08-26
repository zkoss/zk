/* B86_ZK_3977Test.java

        Purpose:
                
        Description:
                
        History:
                Mon Aug 13 10:11:21 CST 2018, Created by charlesqiu

Copyright (C) 2018 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

public class B86_ZK_3977Test extends WebDriverTestCase {

	@Test
	public void test() {
		connect();
		JQuery toggle = findButtonByContent("Toggle");
		JQuery showListenersSize = findButtonByContent("ShowListenersSize");

		click(toggle);
		waitResponse();
		click(showListenersSize);
		waitResponse();
		String listenersSize = getZKLog();
		closeZKLog();

		click(toggle);
		waitResponse();
		click(findButtonByContent("ModifyModel"));
		waitResponse();
		click(toggle);
		waitResponse();

		click(showListenersSize);
		waitResponse();
		Assertions.assertEquals(listenersSize, getZKLog());
		closeZKLog();

		click(findButtonByContent("ShowDataInfo"));
		waitResponse();
		Assertions.assertEquals("[false, false, false, false, false, false][0, 0, 0, 0, 0, 0, 0]", getZKLog());
	}

	private JQuery findButtonByContent(String content) {
		return jq(".z-button:contains(" + content + ")");
	}
}