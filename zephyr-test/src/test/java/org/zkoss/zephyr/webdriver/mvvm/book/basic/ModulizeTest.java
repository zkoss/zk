/* ModulizeTest.java

	Purpose:
		
	Description:
		
	History:
		Wed Apr 28 18:00:32 CST 2021, Created by rudyhuang

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zephyr.webdriver.mvvm.book.basic;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.Keys;

import org.zkoss.zephyr.webdriver.TestStage;
import org.zkoss.zephyr.webdriver.ZephyrClientMVVMTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

/**
 * @author rudyhuang
 */
public class ModulizeTest extends ZephyrClientMVVMTestCase {
	@Test
	public void test() {
		connect("/mvvm/book/basic/modulize.zul");
		JQuery addModule1 = jq("@window $addModule1");
		JQuery addModule2 = jq("@window $addModule2");
		JQuery moduleCount = jq("@window $moduleCount");
		JQuery moduleAmount = jq("@window $moduleAmount");

		click(addModule1);
		waitResponse();
		assertEquals("2", moduleCount.text());
		assertEquals(2, jq("@window @tab").length());

		click(addModule2);
		waitResponse();
		assertEquals("3", moduleCount.text());
		assertEquals(3, jq("@window @tab").length());

		click(jq("@window @tab").eq(2).find(".z-tab-button"));
		waitResponse();
		assertEquals("2", moduleCount.text());
		assertEquals(2, jq("@window @tab").length());

		// Try to decrease the amount by 100
		click(jq("@tab:first"));
		waitResponse();
		JQuery defaultModule1Amount = jq("@window @tabpanel:first-child @window @intbox");
		final int moduleAmountValue = Integer.parseInt(moduleAmount.text());
		int value = Integer.parseInt(defaultModule1Amount.val());
		sendKeys(defaultModule1Amount, Keys.BACK_SPACE, Keys.BACK_SPACE, Keys.BACK_SPACE, String.valueOf(value - 100));
		blur(defaultModule1Amount);
		waitResponse();
		assertEquals(String.valueOf(moduleAmountValue - 100), moduleAmount.text());
	}
}
