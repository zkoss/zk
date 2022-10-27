/* DependsOnTest.java

	Purpose:
		
	Description:
		
	History:
		Wed May 05 11:52:43 CST 2021, Created by rudyhuang

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zephyr.webdriver.mvvm.book.viewmodel.notification;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import org.zkoss.zephyr.webdriver.ClientBindTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

/**
 * @author rudyhuang
 */
public class DependsOnTest extends ClientBindTestCase {
	@Test
	public void test() {
		connect("/mvvm/book/viewmodel/notification/dependson.zul");
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

		click(jq("@button"));
		waitResponse();
		assertEquals("James Riddle", fullname.text());
		assertEquals("James", fn.text());
		assertEquals("Riddle", ln.text());
	}
}
