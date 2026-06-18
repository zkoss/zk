/* B104_ZK_6071Test.java

        Purpose:
                
        Description:
                
        History:
                Tue Apr 28 11:24:58 CST 2026, Created by peakerlee

Copyright (C) 2026 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;

public class B104_ZK_6071Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		waitResponse();

		click(jq("$view"));
		waitResponse();
		sleep(500);

		click(jq("$inner").find(".z-groupbox-title").eq(0));
		waitResponse();
		sleep(500);

		assertTrue(jq("$db").exists(), "datebox should be rendered after inner groupbox is opened");

		String errorJson = getEval("JSON.stringify(window.__jsErrors || [])");
		assertEquals("[]", errorJson, "Expected no JS errors during open sequence; got: " + errorJson);

		assertNoJSError();
	}
}
