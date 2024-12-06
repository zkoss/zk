/* B50_ZK_580Test.java

        Purpose:
                
        Description:
                
        History:
                Fri Nov 29 16:29:59 CST 2024, Created by jamson

Copyright (C) 2024 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.junit.jupiter.api.Assertions.assertNotEquals;

import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.FirefoxWebDriverTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

public class B50_ZK_580Test extends FirefoxWebDriverTestCase {

	@Test
	public void test() {
		connect();
		JQuery textbox = jq(".z-textbox");
		clickAt(textbox, -40, 5);
		waitResponse();
		String originCursorPosition = zk(textbox).eval("getSelectionRange()[0]");

		mouseOver(jq(".z-label"));
		waitResponse();

		clickAt(textbox, -10, 5);
		waitResponse();
		assertNotEquals(originCursorPosition, zk(textbox).eval("getSelectionRange()[0]"));
	}
}
