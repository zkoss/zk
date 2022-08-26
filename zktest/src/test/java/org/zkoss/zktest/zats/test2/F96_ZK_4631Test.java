/* F96_ZK_4631Test.java

	Purpose:
		
	Description:
		
	History:
		Thr Feb 25 12:20:43 CST 2021, Created by katherinelin

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

public class F96_ZK_4631Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		JQuery overflowpopupButton1 = jq(".z-toolbar-overflowpopup-button:eq(0)");
		JQuery overflowpopupButton2 = jq(".z-toolbar-overflowpopup-button:eq(1)");
		Assertions.assertTrue(overflowpopupButton1.hasClass("z-icon-home"));
		Assertions.assertTrue(overflowpopupButton2.hasClass("z-icon-ellipsis-h"));
		click(jq("@button"));
		waitResponse();
		Assertions.assertTrue(overflowpopupButton1.hasClass("z-icon-search"));
	}
}
