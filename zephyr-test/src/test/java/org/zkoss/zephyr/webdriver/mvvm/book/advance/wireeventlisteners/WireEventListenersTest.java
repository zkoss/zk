/* WireEventListenersTest.java

		Purpose:
		
		Description:
		
		History:
				Thu May 06 15:30:06 CST 2021, Created by leon

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zephyr.webdriver.mvvm.book.advance.wireeventlisteners;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;

public class WireEventListenersTest extends WebDriverTestCase {
	@Test
	public void test() {
		connect();

		click(jq("@button"));
		waitResponse();

		assertEquals("onClick", getZKLog());
	}
}
