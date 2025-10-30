/* B96_ZK_4908Test.java

		Purpose:
		
		Description:
		
		History:
				Fri Jul 16 12:28:48 CST 2021, Created by leon

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

public class B96_ZK_4908Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		JQuery nav1 = jq("$nav1");
		JQuery navitem2 = jq("$navitem2");

		click(nav1);
		waitResponse();
		click(navitem2);
		waitResponse();
		Assertions.assertFalse(nav1.hasClass("z-nav-open"), "nav1 shall close");
		// test if the navitem is already selected
		click(nav1);
		waitResponse();
		click(navitem2);
		waitResponse();
		Assertions.assertFalse(nav1.hasClass("z-nav-open"), "nav1 shall close");
	}
}
