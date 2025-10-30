/* B95_ZK_4628Test.java

		Purpose:
		
		Description:
		
		History:
				Tue Nov 17 12:35:40 CST 2020, Created by leon

Copyright (C) 2020 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

public class B95_ZK_4628Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		testTogglePopup(0, true); // test with VM onOpen listener
		testTogglePopup(1, true); // test with onOpen listener
		testTogglePopup(2, false); // test without onOpen listener
	}

	private void testTogglePopup(Integer buttonIndex, boolean haveOnOpenListener) {
		JQuery button = jq("@button").eq(buttonIndex);

		click(button); // open popup
		waitResponse();
		Assertions.assertTrue(jq(".z-popup").isVisible(),
				"should see popop opened");
		if (haveOnOpenListener) {
			Assertions.assertTrue(isZKLogAvailable(),
					"should see onOpen in zk log");
			closeZKLog();
		}

		click(button); // click again to toggle(close) popup
		waitResponse();
		Assertions.assertFalse(jq(".z-popup").isVisible(),
				"should see popop closed");
		if (haveOnOpenListener) {
			Assertions.assertEquals("onOpen", getZKLog(), "should see only one onOpen in zk log");
			closeZKLog();
		}

		click(button); // click again to toggle(open) popup
		waitResponse();
		Assertions.assertTrue(jq(".z-popup").isVisible(),
				"should see popop opened");
		if (haveOnOpenListener) {
			Assertions.assertEquals("onOpen", getZKLog(), "should see only one onOpen in zk log");
			closeZKLog();
		}
	}
}
