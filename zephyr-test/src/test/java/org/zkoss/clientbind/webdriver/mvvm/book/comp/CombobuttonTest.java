/* CombobuttonTest.java

	Purpose:
		
	Description:
		
	History:
		Tue Apr 27 11:21:35 CST 2021, Created by rudyhuang

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.clientbind.webdriver.mvvm.book.comp;

import org.junit.jupiter.api.Test;

import org.zkoss.clientbind.webdriver.ClientBindTestCase;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author rudyhuang
 */
public class CombobuttonTest extends ClientBindTestCase {
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
