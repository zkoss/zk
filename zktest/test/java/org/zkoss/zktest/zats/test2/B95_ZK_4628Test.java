/* B95_ZK_4628Test.java

		Purpose:
		
		Description:
		
		History:
				Tue Nov 17 12:35:40 CST 2020, Created by leon

Copyright (C) 2020 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.Assert;
import org.junit.Test;
import org.zkoss.zktest.zats.WebDriverTestCase;
import org.zkoss.zktest.zats.ztl.JQuery;

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
		Assert.assertTrue("should see popop opened", jq(".z-popup").isVisible());
		if (haveOnOpenListener) {
			Assert.assertTrue("should see onOpen in zk log", isZKLogAvailable());
			closeZKLog();
		}

		click(button); // click again to toggle(close) popup
		waitResponse();
		Assert.assertFalse("should see popop closed", jq(".z-popup").isVisible());
		if (haveOnOpenListener) {
			Assert.assertEquals("should see only one onOpen in zk log", "onOpen", getZKLog());
			closeZKLog();
		}

		click(button); // click again to toggle(open) popup
		waitResponse();
		Assert.assertTrue("should see popop opened", jq(".z-popup").isVisible());
		if (haveOnOpenListener) {
			Assert.assertEquals("should see only one onOpen in zk log", "onOpen", getZKLog());
			closeZKLog();
		}
	}
}
