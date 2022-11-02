/* HugeDataTest.java

		Purpose:
		
		Description:
		
		History:
				Mon May 10 14:18:44 CST 2021, Created by leon

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zephyr.webdriver.mvvm.book.advance.hugedata;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import org.junit.jupiter.api.Test;

import org.zkoss.zephyr.webdriver.ClientBindTestCase;

public class HugeDataTest extends ClientBindTestCase {
	@Test
	public void test() {
		connect();
		click(jq("$logBtn"));
		waitResponse();
		assertEquals("1000", getZKLog());
		assertNotEquals(0, jq("@listitem").length());
	}
}
