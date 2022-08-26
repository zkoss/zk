/* HeaderParamTest.java

		Purpose:
		
		Description:
		
		History:
				Tue May 04 15:10:08 CST 2021, Created by leon

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.bind.advance;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

public class HeaderParamTest extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		String host = getHost() + ":" + getPort();
		JQuery btn = jq("@button");
		Assertions.assertEquals(host, jq("$msg").text());
		Assertions.assertEquals("test", btn.text());

		click(btn);
		waitResponse();
		Assertions.assertEquals(host, btn.text());
	}
}
