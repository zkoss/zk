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

import java.util.logging.Level;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.logging.LogType;

import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

/**
 * @author rudyhuang
 */
public class FlowCtrlTest extends WebDriverTestCase {
	@Test
	public void testChoose() {
		connect("/mvvm/book/shadow/flow/flowctrl-choose.zul");
		waitResponse();
		JQuery navitems = jq("$navbar > @navitem");
		JQuery navs = jq("$navbar > @nav");
		assertEquals(4, navitems.length());
		assertEquals(2, navs.length());
	}

	@Test
	public void testIf() {
		connect("/mvvm/book/shadow/flow/flowctrl-if.zul");
		waitResponse();
		JQuery navitems = jq("$navbar > @navitem");
		JQuery navs = jq("$navbar > @nav");
		assertEquals(4, navitems.length());
		assertEquals(2, navs.length());
	}

	@Test
	public void testOtherwiseWrongUsage() {
		connect("/mvvm/book/shadow/flow/flowctrl-otherwise-wrong.zul");
		assertTrue(driver.manage().logs().get(LogType.BROWSER).getAll().stream()
				.filter(entry -> entry.getLevel().intValue()
						>= Level.SEVERE.intValue()).findFirst().isPresent());
	}

	@Test
	public void testWhenWrongUsage() {
		connect("/mvvm/book/shadow/flow/flowctrl-when-wrong.zul");
		assertTrue(driver.manage().logs().get(LogType.BROWSER).getAll().stream()
				.filter(entry -> entry.getLevel().intValue()
						>= Level.SEVERE.intValue()).findFirst().isPresent());
	}
}
