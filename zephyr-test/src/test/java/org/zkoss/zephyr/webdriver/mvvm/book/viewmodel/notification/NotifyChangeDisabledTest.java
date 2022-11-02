/* NotifyChangeDisabledTest.java

	Purpose:
		
	Description:
		
	History:
		Wed May 05 17:06:42 CST 2021, Created by rudyhuang

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zephyr.webdriver.mvvm.book.viewmodel.notification;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import org.junit.jupiter.api.Test;

import org.zkoss.zephyr.webdriver.ClientBindTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

/**
 * @author rudyhuang
 */
public class NotifyChangeDisabledTest extends ClientBindTestCase {
	@Test
	public void testNormalUsage() {
		connect("/mvvm/book/viewmodel/notification/notifychange-disabled.zul");
		type(jq("$inp0"), "ZK");
		waitResponse();
		assertEquals("ZK", jq("$val0").text());
		type(jq("$inp1"), "ZK");
		assertNotEquals("ZK", jq("$val1").text());
	}

	@Test
	public void testIllegalUsage1() {
		connect("/mvvm/book/viewmodel/notification/notifychange-disabled.zul");
		JQuery jqInp2 = jq("$inp2");
		sendKeys(jqInp2, "ZK");
		waitResponse();
		blur(jqInp2);
		waitResponse();
		assertEquals("don't use interface org.zkoss.bind.annotation.NotifyChange with interface org.zkoss.bind.annotation.NotifyChangeDisabled, choose only one", jq(".z-messagebox .z-label").text());
	}

	@Test
	public void testIllegalUsage2() {
		connect("/mvvm/book/viewmodel/notification/notifychange-disabled.zul");
		JQuery jqInp3 = jq("$inp3");
		sendKeys(jqInp3, "ZK");
		waitResponse();
		blur(jqInp3);
		waitResponse();
		assertEquals("don't use interface org.zkoss.bind.annotation.SmartNotifyChange with interface org.zkoss.bind.annotation.NotifyChangeDisabled, choose only one", jq(".z-messagebox .z-label").text());
	}
}
