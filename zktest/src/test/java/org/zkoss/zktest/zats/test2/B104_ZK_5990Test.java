/* B104_ZK_5990Test.java

        Purpose:
                
        Description:
                
        History:
                Wed Apr 01 10:00:29 CST 2026, Created by peakerlee

Copyright (C) 2026 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.junit.jupiter.api.Assertions.assertFalse;

import org.junit.jupiter.api.Test;
import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

public class B104_ZK_5990Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		// Test each number input component: clear value and blur should not cause NPE
		String[] selectors = {"@intbox", "@doublebox", "@longbox", "@decimalbox", "@spinner", "@doublespinner"};
		String[] inputSelectors = {
			".z-intbox", ".z-doublebox", ".z-longbox", ".z-decimalbox",
			".z-spinner-input", ".z-doublespinner-input"
		};
		for (int i = 0; i < selectors.length; i++) {
			JQuery input = jq(inputSelectors[i]);
			type(input, "");
			waitResponse();
			click(jq("@label"));
			waitResponse();
			assertFalse(hasError(), "NPE error after clearing " + selectors[i]);
		}
	}
}
