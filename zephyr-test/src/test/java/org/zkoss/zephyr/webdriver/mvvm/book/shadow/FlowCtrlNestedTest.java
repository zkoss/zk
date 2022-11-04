/* FlowCtrlNestedTest.java

	Purpose:
		
	Description:
		
	History:
		Fri May 07 15:33:40 CST 2021, Created by rudyhuang

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
public class FlowCtrlNestedTest extends WebDriverTestCase {
	@Test
	public void test() {
		connect("/mvvm/book/shadow/flow/flowctrl-nested.zul");
		click(jq("$btnS"));
		waitResponse();
		JQuery items = jq("@hlayout");
		assertEquals(20, items.length());
		assertEquals("1", getLabelTrimmedValue(items.eq(0).find("@label").eq(0))); // i=1
		assertEquals("Fizz", getLabelTrimmedValue(items.eq(2).find("@label").eq(0))); // i=3
		assertEquals("Buzz", getLabelTrimmedValue(items.eq(4).find("@label").eq(0))); // i=5
		assertEquals("Fizz", getLabelTrimmedValue(items.eq(14).find("@label").eq(0))); // i=15 part1
		assertEquals("Buzz", getLabelTrimmedValue(items.eq(14).find("@label").eq(1))); // i=15 part2

		click(jq("$btnL"));
		waitResponse();
		JQuery items2 = jq("@hlayout");
		assertEquals(51, items2.length());
		assertEquals("Buzz", getLabelTrimmedValue(items2.eq(0).find("@label").eq(0))); // i=50
		assertEquals("Fizz", getLabelTrimmedValue(items2.eq(1).find("@label").eq(0))); // i=51
		assertEquals("52", getLabelTrimmedValue(items2.eq(2).find("@label").eq(0))); // i=52
		assertEquals("Fizz", getLabelTrimmedValue(items2.eq(10).find("@label").eq(0))); // i=60 part1
		assertEquals("Buzz", getLabelTrimmedValue(items2.eq(10).find("@label").eq(1))); // i=60 part2
	}

	private String getLabelTrimmedValue(JQuery lbl) {
		return lbl.text().trim();
	}
}
