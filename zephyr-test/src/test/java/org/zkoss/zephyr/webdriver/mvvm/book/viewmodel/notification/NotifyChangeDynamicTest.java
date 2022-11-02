/* NotifyChangeDynamicTest.java

	Purpose:
		
	Description:
		
	History:
		Wed May 05 15:52:57 CST 2021, Created by rudyhuang

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
public class NotifyChangeDynamicTest extends ClientBindTestCase {
	@Test
	public void test() {
		connect("/mvvm/book/viewmodel/notification/notifychange-dynamic.zul");
		final JQuery add = jq("$add");
		final JQuery addNoNotify = jq("$addNoNotify");
		final JQuery current = jq("$current");
		click(add);
		waitResponse();
		click(add);
		waitResponse();
		click(add);
		waitResponse();
		assertEquals("15", current.text());

		click(addNoNotify);
		waitResponse();
		click(addNoNotify);
		waitResponse();
		click(addNoNotify);
		waitResponse();
		assertEquals("15", current.text());

		click(add);
		waitResponse();
		assertEquals("35", current.text());
	}
}
