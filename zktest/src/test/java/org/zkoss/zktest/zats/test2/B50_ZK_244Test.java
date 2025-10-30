/* B50_ZK_244Test.java

		Purpose:
		
		Description:
		
		History:
				Wed Apr 03 11:45:21 CST 2019, Created by leon

Copyright (C) 2019 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

public class B50_ZK_244Test extends WebDriverTestCase {
	@Test
	public void test(){
		connect();
		JQuery body = jq("body");
		JQuery zwindow = jq(".z-window");
		click(jq(".z-button"));
		waitResponse();
		click(jq(".z-window-maximize"));
		waitResponse();
		Assertions.assertEquals(body.outerWidth(), zwindow.outerWidth());
		Assertions.assertEquals(body.outerHeight(), zwindow.outerHeight());
	}
}
