/* B95_ZK_4720Test.java

	Purpose:
		
	Description:
		
	History:
		Mon Dec 14 16:53:05 CST 2020, Created by rudyhuang

Copyright (C) 2020 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;

/**
 * @author rudyhuang
 */
public class B95_ZK_4720Test extends WebDriverTestCase {
	@Test
	public void testLayout() {
		connect();

		jq("$d1").scrollTop(100);
		jq("$d2").scrollTop(100);
		jq("$d3").scrollTop(100);
		click(jq("@checkbox:eq(0)"));
		waitResponse();
		click(jq("@checkbox:eq(0)"));
		waitResponse();
		assertNoJSError();
	}

	@Test
	public void testBox() {
		connect();

		jq("$d4").scrollTop(100);
		jq("$d5").scrollTop(100);
		jq("$d6").scrollTop(100);
		click(jq("@checkbox:eq(1)"));
		waitResponse();
		click(jq("@checkbox:eq(1)"));
		waitResponse();
		assertNoJSError();
	}
}
