/* HeaderParamTest.java

		Purpose:
		
		Description:
		
		History:
				Tue May 04 15:10:08 CST 2021, Created by leon

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zephyr.webdriver.mvvm.book.advance.parameters;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

public class HeaderParamTest extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		String host = getHost() + ":" + getPort();
		JQuery btn = jq("@button");
		assertEquals(host, jq("$msg").text());
		assertEquals("test", btn.text());

		click(btn);
		waitResponse();
		assertEquals(host, btn.text());
	}
}
