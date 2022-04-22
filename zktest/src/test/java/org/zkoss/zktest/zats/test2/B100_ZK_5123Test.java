/* B100_ZK_5123Test.java

	Purpose:

	Description:

	History:
		Wed Mar 09 11:45:40 CST 2022, Created by katherine

Copyright (C) 2022 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import org.zkoss.zktest.zats.WebDriverTestCase;

public class B100_ZK_5123Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		waitResponse();
		assertTrue(jq(".z-searchbox-popup").isVisible());
	}
}
