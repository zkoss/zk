/* CombobuttonTest.java

	Purpose:
		
	Description:
		
	History:
		Tue Apr 27 11:21:35 CST 2021, Created by rudyhuang

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zephyr.webdriver.mvvm.book.comp;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import org.zkoss.zephyr.webdriver.TestStage;
import org.zkoss.zephyr.webdriver.ZephyrClientMVVMTestCase;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author rudyhuang
 */
public class CombobuttonTest extends ZephyrClientMVVMTestCase {
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
