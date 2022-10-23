/* FlowCtrlTest.java

	Purpose:
		
	Description:
		
	History:
		Thu May 06 18:13:58 CST 2021, Created by rudyhuang

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zephyr.webdriver.mvvm.book.shadow;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.ztl.JQuery;
import org.zkoss.zephyr.webdriver.TestStage;
import org.zkoss.zephyr.webdriver.ZephyrClientMVVMTestCase;

/**
 * @author rudyhuang
 */
public class FlowCtrlTest extends ZephyrClientMVVMTestCase {
	@Test
	public void testChoose() {
		connect("/mvvm/book/shadow/flow/flowctrl-choose.zul");
		JQuery navitems = jq("$navbar > @navitem");
		JQuery navs = jq("$navbar > @nav");
		assertEquals(4, navitems.length());
		assertEquals(2, navs.length());
	}

	@Test
	public void testIf() {
		connect("/mvvm/book/shadow/flow/flowctrl-if.zul");
		JQuery navitems = jq("$navbar > @navitem");
		JQuery navs = jq("$navbar > @nav");
		assertEquals(4, navitems.length());
		assertEquals(2, navs.length());
	}

	@Test
	public void testOtherwiseWrongUsage() {
		connect("/mvvm/book/shadow/flow/flowctrl-otherwise-wrong.zul");
		waitResponse();
		assertTrue(hasError());
	}

	@Test
	public void testWhenWrongUsage() {
		connect("/mvvm/book/shadow/flow/flowctrl-when-wrong.zul");
		waitResponse();
		assertTrue(hasError());
	}
}
