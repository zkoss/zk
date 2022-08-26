/* B96_ZK_4806Test.java

		Purpose:
		
		Description:
		
		History:
				Mon Apr 12 16:45:09 CST 2021, Created by leon

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

public class B96_ZK_4806Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		checkHeaderHeight("north");
		checkHeaderHeight("west");
		checkHeaderHeight("east");
		checkHeaderHeight("south");
	}

	private void checkHeaderHeight(String regionName) {
		JQuery headers = jq(".z-" + regionName + "-header");
		Assertions.assertEquals(headers.eq(1).outerHeight(), headers.eq(0).outerHeight(),
				"region with whitespace title should have same header height to the region with normal title.");
	}
}
