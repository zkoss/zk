/* B86_ZK_3763Test.java

		Purpose:

		Description:

		History:
				Thu Oct 04 16:45:04 CST 2018, Created by leon

Copyright (C) 2018 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;

public class B86_ZK_3763Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		click(jq("$btn1"));
		waitResponse();
		jq("@hlayout:eq(0)").scrollLeft(2000);
		waitResponse();
		click(jq("$btn2"));
		waitResponse();
		jq("html").scrollTop(1000);
		waitResponse();
		click(jq("$btn4"));
		waitResponse();
		jq("html").scrollLeft(2000);
		waitResponse();
		click(jq("$btn3"));
		waitResponse();
		Assertions.assertEquals(4, jq(".z-notification-content").length());
	}
}
