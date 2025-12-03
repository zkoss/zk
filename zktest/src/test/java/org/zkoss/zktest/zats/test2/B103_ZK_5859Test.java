/* B103_ZK_5859Test.java

	Purpose:

	Description:

	History:
		Sun Nov 03 16:38:09 CST 2025, Created by peaker

Copyright (C) 2025 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;

public class B103_ZK_5859Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();

		click(jq("@button:contains(trigger: open popup 2x)"));
		waitResponse();
		click(jq("@button:contains(Close Popup)"));
		waitResponse();

		click(jq("@button:contains(trigger: open popup 2x)"));
		waitResponse();
		click(jq("@button:contains(Close Popup)"));
		waitResponse();

		click(jq("@button:contains(detach trigger)"));
		waitResponse();

		assertNoAnyError();
	}
}
