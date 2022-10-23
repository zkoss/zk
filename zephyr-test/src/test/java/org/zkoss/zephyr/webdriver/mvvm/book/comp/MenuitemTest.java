/* MenuitemTest.java

	Purpose:
		
	Description:
		
	History:
		Tue Apr 27 12:44:08 CST 2021, Created by rudyhuang

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zephyr.webdriver.mvvm.book.comp;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import org.zkoss.zephyr.webdriver.TestStage;
import org.zkoss.zephyr.webdriver.ZephyrClientMVVMTestCase;

/**
 * @author rudyhuang
 */
public class MenuitemTest extends ZephyrClientMVVMTestCase {
	@Test
	public void test() {
		connect();
		click(jq("@menu"));
		waitResponse();
		click(jq("@menuitem").last());
		waitResponse();
		assertEquals("true", jq("$checked").text());
		click(jq("@menu"));
		waitResponse();
		click(jq("@menuitem").last());
		waitResponse();
		assertEquals("false", jq("@window $checked").text());
	}
}
