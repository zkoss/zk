/* B95_ZK_4691Test.java

	Purpose:
		
	Description:
		
	History:
		The Nov 12 09:50:21 CST 2020, Created by jameschu

Copyright (C) 2020 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

import org.zkoss.zktest.zats.WebDriverTestCase;

/**
 * @author jameschu
 */
public class B95_ZK_4691Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		waitResponse();
		closeZKLog();
		waitResponse();
		click(jq("$chgURI"));
		waitResponse();
		assertEquals("B.zul", jq("@div").find("@label").html().trim());
		assertEquals(false, isZKLogAvailable());
	}
}
