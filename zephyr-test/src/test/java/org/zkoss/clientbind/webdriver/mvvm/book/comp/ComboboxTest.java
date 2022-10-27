/* ComboboxTest.java

	Purpose:
		
	Description:
		
	History:
		Tue Apr 27 10:47:14 CST 2021, Created by rudyhuang

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.clientbind.webdriver.mvvm.book.comp;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import org.zkoss.clientbind.webdriver.ClientBindTestCase;

/**
 * @author rudyhuang
 */
public class ComboboxTest extends ClientBindTestCase {
	@Test
	public void test() {
		connect();
		click(jq(".z-combobox-button"));
		waitResponse();
		assertEquals("true", jq("$open").text());
		click(jq(".z-combobox-button"));
		waitResponse();
		assertEquals("false", jq("$open").text());
	}
}
