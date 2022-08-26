/* ClientBindingTest.java
	Purpose:

	Description:

	History:
		Wed May 12 16:17:56 CST 2021, Created by jameschu

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.bind.databinding.clientbinding;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;

/**
 * @author jameschu
 */
public class ClientBindingTest extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		//[Step 1]
		click(jq("$clientBtn"));
		waitResponse();
		assertTrue(isZKLogAvailable());
		closeZKLog();
		click(jq("$serverBtn"));
		waitResponse();
		assertTrue(isZKLogAvailable());
		closeZKLog();

		//[Step 2]
		click(jq("$clientOnClickBtn"));
		waitResponse();
		assertTrue(getZKLog().contains("Client namespace"));
		closeZKLog();
		click(jq("#nativeBtn"));
		waitResponse();
		assertTrue(getZKLog().contains("Native Component"));
		closeZKLog();
		//[Step 3]
		click(jq("#zhtmlServerBtn"));
		waitResponse();
		assertTrue(isZKLogAvailable());
		closeZKLog();
		click(jq("#zhtmlClientAttrBtn"));
		waitResponse();
		assertTrue(isZKLogAvailable());
		closeZKLog();
		click(jq("#zhtmlClientBtn"));
		waitResponse();
		assertTrue(getZKLog().contains("zhtml button client"));
		closeZKLog();
		//[Step 4]
		assertEquals("0", jq("$cnt").text());
		click(jq("$pureAddBtn"));
		waitResponse();
		assertEquals("0", jq("$cnt").text());
		//[Step 5]
		click(jq("$addBtn"));
		waitResponse();
		assertEquals("2", jq("$cnt").text());
		assertEquals("after command \"notifyClient\" (1)", jq("#span").text());
	}
}
