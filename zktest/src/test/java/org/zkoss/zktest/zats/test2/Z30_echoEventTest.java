/* Z30_echoEventTest.java

        Purpose:
                
        Description:
                
        History:
                Thu Feb 01 15:58:22 CST 2024, Created by jamson

Copyright (C) 2024 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;

public class Z30_echoEventTest extends WebDriverTestCase {

	@Test
	public void test() throws InterruptedException {
		connect();

		assertEquals(1, getLabelLength());

		click(jq("@button"));
		Thread.sleep(1000);
		assertEquals(2, getLabelLength());

		waitResponse();
		assertEquals(3, getLabelLength());
	}

	public int getLabelLength() {
		return jq("$w").find("@label").length();
	}
}
