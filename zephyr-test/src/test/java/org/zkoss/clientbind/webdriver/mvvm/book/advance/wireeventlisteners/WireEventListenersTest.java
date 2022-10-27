/* WireEventListenersTest.java

		Purpose:
		
		Description:
		
		History:
				Thu May 06 15:30:06 CST 2021, Created by leon

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.clientbind.webdriver.mvvm.book.advance.wireeventlisteners;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import org.zkoss.clientbind.webdriver.ClientBindTestCase;

public class WireEventListenersTest extends ClientBindTestCase {
	@Test
	public void test() {
		connect();

		click(jq("@button"));
		waitResponse();

		assertEquals("onClick", getZKLog());
	}
}
