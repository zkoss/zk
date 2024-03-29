/* B86_ZK_3647Test.java

        Purpose:
                
        Description:
                
        History:
                Fri Aug 31 17:37:18 CST 2018, Created by charlesqiu

Copyright (C) 2018 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

public class B86_ZK_3647Test extends WebDriverTestCase {

	@Test
	public void test() {
		connect();
		JQuery logScrollTop = jq(".z-button:contains(logScrollTop)");

		click(jq(".z-button:contains(scroll)"));
		waitResponse();

		click(logScrollTop);
		waitResponse();
		String scrollTop = getZKLog();
		closeZKLog();
		waitResponse();

		click(jq(".z-listheader"));
		waitResponse();

		click(logScrollTop);
		waitResponse();
		Assertions.assertEquals(scrollTop, getZKLog());
	}
}
