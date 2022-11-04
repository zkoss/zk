/* GroupboxTest.java

	Purpose:
		
	Description:
		
	History:
		Tue Apr 27 12:17:11 CST 2021, Created by rudyhuang

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zephyr.webdriver.mvvm.book.comp;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;

/**
 * @author rudyhuang
 */
public class GroupboxTest extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		click(jq(".z-groupbox .z-caption"));
		waitResponse();
		assertEquals("true", jq("$openStatus").text());
		click(jq(".z-groupbox .z-caption"));
		waitResponse();
		assertEquals("false", jq("$openStatus").text());
	}
}
