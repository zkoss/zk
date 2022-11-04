/* CombobuttonTest.java

	Purpose:
		
	Description:
		
	History:
		Tue Apr 27 11:21:35 CST 2021, Created by rudyhuang

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zephyr.webdriver.mvvm.book.comp;

import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author rudyhuang
 */
public class CombobuttonTest extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		click(jq(".z-combobutton-button"));
		waitResponse();
		assertEquals("true", jq("$open").text());
		click(jq(".z-combobutton-button"));
		waitResponse();
		assertEquals("false", jq("$open").text());
	}
}
