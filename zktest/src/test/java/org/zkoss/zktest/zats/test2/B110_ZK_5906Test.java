/* B110_ZK_5906Test.java

        Purpose:

        Description:

        History:
                Tue May 19 15:20:59 CST 2026, Created by peakerlee

Copyright (C) 2026 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.junit.jupiter.api.Assertions.assertFalse;

import org.junit.jupiter.api.Test;
import org.zkoss.test.webdriver.WebDriverTestCase;

public class B110_ZK_5906Test extends WebDriverTestCase {
	@Test
	public void anchorClickBeforeUnload() {
		clickThenUnload("$lnk");
	}

	@Test
	public void buttonClickBeforeUnload() {
		clickThenUnload("$btn");
	}

	private void clickThenUnload(String selector) {
		connect();
		eval("jq('" + selector + "')[0].click(), window.onbeforeunload({})");
		waitResponse(true);
		assertFalse(jq(".z-messagebox-window").exists());
	}
}
