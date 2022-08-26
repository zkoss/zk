/* DrawerTest.java

	Purpose:
		
	Description:
		
	History:
		Thu Jun 11 11:24:52 CST 2020, Created by rudyhuang

Copyright (C) 2020 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.wcag;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.Keys;

/**
 * @author rudyhuang
 */
public class DrawerTest extends WcagTestCase {
	@Test
	public void test() {
		connect();

		click(jq("@button:eq(0)"));
		waitResponse(true);
		verifyA11y();
		click(widget("$d1").$n("close"));
		waitResponse(true);

		click(jq("@button:eq(1)"));
		waitResponse(true);
		verifyA11y();
	}

	@Test
	public void testKeyDownPropagation() {
		connect();

		click(jq("@button:eq(0)"));
		waitResponse(true);

		click(jq("@textbox"));
		waitResponse();
		sendKeys(jq("@textbox"), Keys.ESCAPE);
		waitResponse();
		Assertions.assertTrue(jq("@drawer").isVisible());
	}
}
