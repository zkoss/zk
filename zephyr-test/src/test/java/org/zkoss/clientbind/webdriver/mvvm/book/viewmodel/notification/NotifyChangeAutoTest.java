/* NotifyChangeOnSetterTest.java

	Purpose:
		
	Description:
		
	History:
		Wed May 05 11:52:43 CST 2021, Created by rudyhuang

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.clientbind.webdriver.mvvm.book.viewmodel.notification;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import org.junit.jupiter.api.Test;

import org.zkoss.clientbind.webdriver.ClientBindTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

/**
 * @author rudyhuang
 */
public class NotifyChangeAutoTest extends ClientBindTestCase {
	@Test
	public void testByAnnotation() {
		connect("/mvvm/book/viewmodel/notification/notifychange-auto.zul");
		final JQuery fullname = jq("$fullname");
		final JQuery fn = jq("$firstname");
		final JQuery ln = jq("$lastname");
		click(jq("@button"));
		waitResponse();
		assertNotEquals("John", fn.text());
		assertNotEquals("Smith", ln.text());
		assertEquals("John Smith", fullname.text()); // spec see ZK-4891
	}

	@Test
	public void testBySysProp() {
		connect("/mvvm/book/viewmodel/notification/notifychange-auto-sysprop.zul");
		try {
			final JQuery fullname = jq("$fullname");
			final JQuery fn = jq("$firstname");
			final JQuery ln = jq("$lastname");
			click(jq("$rnd"));
			waitResponse();
			assertNotEquals("John", fn.text());
			assertNotEquals("Smith", ln.text());
			assertEquals("John Smith", fullname.text()); // spec see ZK-4891
		} finally {
			click(jq("$restore"));
		}
	}
}
