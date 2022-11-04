/* ForeachTest.java

	Purpose:
		
	Description:
		
	History:
		Thu May 06 15:24:34 CST 2021, Created by rudyhuang

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zephyr.webdriver.mvvm.book.shadow;

import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author rudyhuang
 */
public class ForeachTest extends WebDriverTestCase {
	@Test
	public void testItems() {
		connect("/mvvm/book/shadow/iterate/foreach-items.zul");
		JQuery navitems = jq("$navbar > @navitem");
		assertEquals(6, navitems.length());
	}

	@Test
	public void testNumbers() {
		connect("/mvvm/book/shadow/iterate/foreach-numbers.zul");
		final JQuery multiplicationTable = jq("$multiplicationTable");
		assertEquals(5, multiplicationTable.find("@vlayout").length());
		assertEquals(50, multiplicationTable.find("@label").length());
	}

	@Test
	public void testNumbersStepMinus() {
		//TODO: step restriction
		//		Throwable t = Assert.assertThrows(ZatsException.class, () -> {
		//			connect("/mvvm/book/shadow/foreach-numbers-step-minus.zul");
		//		});
		//		assertEquals(IllegalArgumentException.class, t.getCause().getClass());
	}

	@Test
	public void testNumbersReverse() {
		connect("/mvvm/book/shadow/iterate/foreach-numbers-reverse.zul");
		final JQuery multiplicationTable = jq("$multiplicationTable");
		assertEquals(5, multiplicationTable.find("@vlayout").length());
		assertEquals(0, multiplicationTable.find("@label").length());
	}
}
