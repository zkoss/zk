/* B50_ZK_275Test.java

		Purpose:
		
		Description:
		
		History:
				Wed Apr 03 12:42:29 CST 2019, Created by leon

Copyright (C) 2019 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

public class B50_ZK_275Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		JQuery body = jq("body");
		JQuery zwindow = jq(".z-window");
		JQuery maxbutton = jq(".z-window-maximize");
		
		click(jq("@button:contains(add Caption)"));
		waitResponse();
		Assertions.assertTrue(jq(".z-caption").exists());
		Assertions.assertFalse(isZKLogAvailable());
		
		click(maxbutton);
		waitResponse();
		Assertions.assertEquals(body.outerWidth(), zwindow.outerWidth());
		Assertions.assertEquals(body.outerHeight(), zwindow.outerHeight());
		
		click(maxbutton);
		waitResponse();
		click(jq("@button:contains(remove Caption)"));
		waitResponse();
		Assertions.assertFalse(jq(".z-caption").exists());
		Assertions.assertFalse(isZKLogAvailable());
	}
}
