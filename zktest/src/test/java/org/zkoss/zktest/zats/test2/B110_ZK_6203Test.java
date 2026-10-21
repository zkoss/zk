/* B110_ZK_6203Test.java

	Purpose:

	Description:

	History:
		Tue Oct 20 15:35:32 CST 2026, Created by peggypeng

Copyright (C) 2026 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.Keys;

import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

@Tag("WcagTestOnly")
public class B110_ZK_6203Test extends WebDriverTestCase {
	@Test
	public void testCancel() {
		connect();

		sendKeys(jq("$win").find(".z-intbox"), "0");
		click(jq("@button"));
		Assertions.assertTrue(jq(".z-errorbox").exists(), "Clicking Cancel should leave the intbox and open its error box");
		waitResponse(true);
		Assertions.assertFalse(jq("$win").exists(), "Cancel should close the window");
		assertNoJSError();
	}

	@Test
	public void testCorrectWhileOpening() {
		connect();
		JQuery quantity = jq("$quantity");

		// sendKeys focuses without a click: a Tab that follows a click before zk._cfByMD is reset skips validation
		sendKeys(quantity, "0", Keys.TAB);
		// not waitResponse(true): 5 must close the error box while it is still fading in
		sleep(100);
		Assertions.assertTrue(jq(".z-errorbox").isVisible(), "0 should open the error box");

		sendKeys(quantity, Keys.BACK_SPACE, "5", Keys.TAB);
		waitResponse(true);
		Assertions.assertFalse(jq(".z-errorbox").exists(), "5 should close the error box");
		assertNoJSError();
	}
}
