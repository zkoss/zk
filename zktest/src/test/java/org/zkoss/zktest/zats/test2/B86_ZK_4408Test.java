/* B86_ZK_4408Test.java

		Purpose:
		
		Description:
		
		History:
				Tue Jan 14 17:16:03 CST 2020, Created by leon

Copyright (C) 2020 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

public class B86_ZK_4408Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		int bandboxWidth = jq(".z-bandbox").outerWidth();
		JQuery jqBtns = jq("@button");
		JQuery jqWindow = jq("@window");
		JQuery jqBandboxButton = jq(".z-bandbox-button");
		
		click(jqBtns.eq(0));
		waitResponse(true);
		click(jqWindow);
		waitResponse(true);
		click(jqBandboxButton);
		waitResponse();
		Assertions.assertEquals(bandboxWidth, jq(".z-bandbox").outerWidth(), "bandbox width should not change");
		
		click(jqBtns.eq(1));
		waitResponse(true);
		click(jqWindow);
		waitResponse(true);
		click(jqBandboxButton);
		waitResponse();
		Assertions.assertTrue(jq(".z-bandbox-popup").isVisible());
	}
}