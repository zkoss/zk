/** F1Test.java.

	Purpose:
		
	Description:
		
	History:
		2:02:05 PM Dec 31, 2014, Created by jumperchen

Copyright (C) 2014 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zephyr.webdriver.mvvm.book.databinding.form;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

/**
 * @author jumperchen
 */
public class F1Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		waitResponse();
		JQuery registerButton = jq("$registerButton");
		JQuery message = jq("$message");
		final String msg = message.text();
		click(registerButton);
		waitResponse();
		assertEquals(msg, message.text());


		JQuery accountBox = jq("$accountBox");
		type(accountBox, "john");
		waitResponse();
		JQuery passwordBox = jq("$passwordBox");
		type(passwordBox, "1");
		waitResponse();
		JQuery passwordBox2 = jq("$passwordBox2");
		type(passwordBox2, "2");
		waitResponse();
		click(registerButton);
		waitResponse();
		assertEquals(msg, message.text());
		type(passwordBox2, "1");
		waitResponse();
		click(registerButton);
		waitResponse();
		assertEquals("Hi, john: You are NOT an adult.", message.text());
		type(jq("$birthdayBox input"), "1978/1/1");
		waitResponse();
		click(registerButton);
		waitResponse();
		assertEquals("Hi, john: You are an adult.", message.text());
	}
}
