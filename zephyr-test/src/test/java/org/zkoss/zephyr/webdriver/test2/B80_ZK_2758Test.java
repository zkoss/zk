/* B80_ZK_2758Test.java

	Purpose:
		
	Description:
		
	History:
		Fri May 29 12:22:25 CST 2015, Created by jumperchen

Copyright (C)  Potix Corporation. All Rights Reserved.

This program is distributed under LGPL Version 2.1 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
package org.zkoss.zephyr.webdriver.test2;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import org.zkoss.zephyr.webdriver.ZephyrClientMVVMTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

/**
 * @author jumperchen
 */
public class B80_ZK_2758Test extends ZephyrClientMVVMTestCase {
	@Test
	public void test() {
		connect();
		sleep(2000);
		JQuery main = jq("$main");
		JQuery button = main.find("@button");
		JQuery groups = main.find(".form-group");
		assertFalse(groups.eq(0).hasClass("has-error"));
		assertFalse(groups.eq(1).hasClass("has-error"));
		click(button);
		waitResponse();
		assertTrue(groups.eq(0).hasClass("has-error"));
		assertTrue(groups.eq(1).hasClass("has-error"));
		click(button);
		waitResponse();
		assertFalse(groups.eq(0).hasClass("has-error"));
		assertFalse(groups.eq(1).hasClass("has-error"));
	}

	@Test
	public void test1() {
		test();
	}

	@Test
	public void test2() {
		test();
	}
}
