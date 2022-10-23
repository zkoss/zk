/* MVP2MVVMTest.java

	Purpose:
		
	Description:
		
	History:
		Wed Apr 28 16:34:28 CST 2021, Created by rudyhuang

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zephyr.webdriver.mvvm.book.basic;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import org.zkoss.zephyr.webdriver.TestStage;
import org.zkoss.zephyr.webdriver.ZephyrClientMVVMTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

/**
 * @author rudyhuang
 */
public class MVP2MVVMTest extends ZephyrClientMVVMTestCase {
	@Test
	public void test() {
		connect("/mvvm/book/basic/mvp2mvvm_mvp.zul");
		JQuery buttonsOutside = jq("@window > @button");
		JQuery buttonInside = jq("@window $mWinA $innerToggle");
		JQuery textbox = jq("@window $mWinA $textA");

		click(buttonsOutside.get(0));
		waitResponse();
		assertFalse(textbox.is(":disabled"));
		click(buttonsOutside.get(0));
		waitResponse();
		assertTrue(textbox.is(":disabled"));
		click(buttonsOutside.get(1));
		waitResponse();
		assertFalse(textbox.is(":disabled"));
		click(buttonsOutside.get(1));
		waitResponse();
		assertTrue(textbox.is(":disabled"));

		click(buttonInside);
		waitResponse();
		assertFalse(textbox.is(":disabled"));
		click(buttonInside);
		waitResponse();
		assertTrue(textbox.is(":disabled"));
	}
}
