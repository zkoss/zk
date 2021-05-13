/* ClientBindingTest.java
	Purpose:

	Description:

	History:
		Wed May 12 16:17:56 CST 2021, Created by jameschu

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.bind.databinding.clientbinding;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.zkoss.zktest.zats.WebDriverTestCase;

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
		assertEquals("0", jq("$cnt").text());
		click(jq("$pureAddBtn"));
		waitResponse();
		assertEquals("0", jq("$cnt").text());
		//[Step 3]
		click(jq("$addBtn"));
		waitResponse();
		assertEquals("2", jq("$cnt").text());
		assertEquals("after command \"notifyClient\" (1)", jq("#span").text());
	}
}
