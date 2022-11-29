/* B95_ZK_4691Test.java

	Purpose:
		
	Description:
		
	History:
		The Nov 12 09:50:21 CST 2020, Created by jameschu

Copyright (C) 2020 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;

/**
 * @author jameschu
 */
public class B95_ZK_4691Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		waitResponse();
		assertEquals(1, jq("@div").length());
		assertEquals(2, jq("@div").find("@label").length());
		closeZKLog();
		waitResponse();
		click(jq("$chgURI"));
		waitResponse();
		assertEquals(1, jq("@div").length());
		assertEquals(1, jq("@div").find("@label").length());
		assertEquals("B.zul", jq("@div").find("@label").html().trim());
		assertEquals(false, isZKLogAvailable());
	}
}
