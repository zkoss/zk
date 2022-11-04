/* Radiogroup_selectedTest.java

	Purpose:
		
	Description:
		
	History:
		Tue Apr 27 16:20:09 CST 2021, Created by rudyhuang

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zephyr.webdriver.mvvm.book.comp;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;

/**
 * @author rudyhuang
 */
public class Radiogroup_selectedTest extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		// All selected B
		assertTrue(jq("$itemb").hasClass("z-listitem-selected"));
		assertTrue(jq("$radiob").hasClass("z-radio-on"));

		// Select Item A
		click(jq("$itema"));
		waitResponse();
		assertTrue(jq("$radioa").hasClass("z-radio-on"));

		// Select Radio C
		click(jq("$radioc"));
		waitResponse();
		assertTrue(jq("$itemc").hasClass("z-listitem-selected"));
	}
}
