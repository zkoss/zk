/* B110_ZK_6107Test.java

        Purpose:
                
        Description:
                
        History:
                Fri Jul 24 16:22:03 CST 2026, Created by peakerlee

Copyright (C) 2026 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;

public class B110_ZK_6107Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		waitResponse();

		assertTrue(jq(".lm_title:contains(Console)").exists(), "panels should render");
		assertEquals("5", getEval("jq('.lm_tab').length"));

		dragPanelOnto("Console", "Side");
		assertNoJSError();
		assertEquals("5", getEval("jq('.lm_tab').length"));

		dragPanelOnto("Files", "Side");
		assertNoJSError();
		assertEquals("5", getEval("jq('.lm_tab').length"));
	}

	private void dragPanelOnto(String from, String to) {
		getActions().moveToElement(toElement(jq(".lm_title:contains(" + from + ")")))
				.clickAndHold()
				.moveByOffset(15, 15)
				.moveToElement(toElement(jq(".lm_title:contains(" + to + ")")))
				.release()
				.perform();
		waitResponse();
	}
}
