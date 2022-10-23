/* NotifyChangeTest.java

	Purpose:
		
	Description:
		
	History:
		Wed May 05 14:22:21 CST 2021, Created by rudyhuang

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zephyr.webdriver.mvvm.book.viewmodel.notification;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.Keys;

import org.zkoss.zephyr.webdriver.TestStage;
import org.zkoss.zephyr.webdriver.ZephyrClientMVVMTestCase;

/**
 * @author rudyhuang
 */
public class NotifyChangeTest extends ZephyrClientMVVMTestCase {
	@Test
	public void testNotifySelf() {
		connect("/mvvm/book/viewmodel/notification/notifychange-self.zul");
		sendKeys(jq("$date1 .z-datebox-input"), Keys.HOME, Keys.chord(Keys.SHIFT, Keys.END), Keys.DELETE, "2021/05/01", Keys.TAB);
		waitResponse();
		sendKeys(jq("$date2 .z-datebox-input"), Keys.HOME, Keys.chord(Keys.SHIFT, Keys.END), Keys.DELETE, "2021/05/08", Keys.TAB);
		waitResponse();
		assertEquals("7", jq("$dur").text());
	}

	@Test
	public void testNotifyAsterisk() {
		connect("/mvvm/book/viewmodel/notification/notifychange-asterisk.zul");
		sendKeys(jq("$date1 .z-datebox-input"), Keys.HOME, Keys.chord(Keys.SHIFT, Keys.END), Keys.DELETE, "2021/05/01", Keys.TAB);
		waitResponse();
		sendKeys(jq("$date2 .z-datebox-input"), Keys.HOME, Keys.chord(Keys.SHIFT, Keys.END), Keys.DELETE, "2021/05/08", Keys.TAB);
		waitResponse();
		assertNotEquals("7", jq("$dur").text());
	}
}
