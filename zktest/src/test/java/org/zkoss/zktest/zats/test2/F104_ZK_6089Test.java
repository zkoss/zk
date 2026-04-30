/* F104_ZK_6089Test.java

        Purpose:
                
        Description:
                
        History:
                Thu Apr 30 17:26:22 CST 2026, Created by peakerlee

Copyright (C) 2026 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

public class F104_ZK_6089Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		waitResponse();
		JQuery msg = jq("$msg");
		assertTrue(msg.exists(), "Label widget '$msg' should exist");
		assertEquals("loaded-from-classpath", msg.text(),
				"Label loaded via ~./ prefix should resolve from classpath");
	}
}
