/* NotifyChangeOnSetterTest.java

	Purpose:
		
	Description:
		
	History:
		Wed May 05 11:52:43 CST 2021, Created by rudyhuang

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.clientbind.webdriver.mvvm.book.viewmodel.notification;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.Keys;

import org.zkoss.clientbind.webdriver.ClientBindTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

/**
 * @author rudyhuang
 */
public class NotifyChangeOnSetterTest extends ClientBindTestCase {
	@Test
	public void test() {
		connect("/mvvm/book/viewmodel/notification/notifychange-onsetter.zul");
		final JQuery fullname = jq("$fullname");
		final JQuery firstname = jq("$firstname");
		final JQuery lastname = jq("$lastname");
		assertEquals("John Smith", fullname.text());
		assertEquals("John", firstname.text());
		assertEquals("Smith", lastname.text());

		sendKeys(jq("$fn"), Keys.HOME, Keys.chord(Keys.SHIFT, Keys.END), Keys.DELETE, "Tom", Keys.TAB);
		sendKeys(jq("$ln"), Keys.HOME, Keys.chord(Keys.SHIFT, Keys.END), Keys.DELETE, "Riddle", Keys.TAB);
		waitResponse();
		assertEquals("Tom Riddle", fullname.text());
		assertEquals("John", firstname.text()); //error, might be spec change??
		assertEquals("Riddle", lastname.text());
	}

	@Test
	public void testSmartCombination() {
		connect("/mvvm/book/viewmodel/notification/notifychange-onsetter2.zul");
		final JQuery fullname = jq("$fullname");
		final JQuery fn = jq("$firstname");
		final JQuery ln = jq("$lastname");
		assertEquals("John Smith", fullname.text());
		assertEquals("John", fn.text());
		assertEquals("Smith", ln.text());

		type(jq("$fn"), "Tom");
		waitResponse();
		type(jq("$ln"), "Riddle");
		waitResponse();
		assertEquals("Tom Riddle", fullname.text());
		assertEquals("Tom", fn.text());
		assertEquals("Riddle", ln.text());
	}
}
