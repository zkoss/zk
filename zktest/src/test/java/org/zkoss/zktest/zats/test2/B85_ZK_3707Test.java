/* B85_ZK_3707Test.java

	Purpose:
		
	Description:
		
	History:
		Thu Nov 2 11:32:39 CST 2017, Created by jameschu

Copyright (C) 2017 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;

/**
 * @author jameschu
 */
public class B85_ZK_3707Test extends WebDriverTestCase {
	@Test
	public void testOverrideAuOnResp() throws Exception {
		connect();
		waitResponse();
		assertEquals("row-resize", jq(".z-splitlayout-splitter-draggable").eq(0).css("cursor"));
		assertEquals("col-resize", jq(".z-splitlayout-splitter-draggable").eq(1).css("cursor"));
	}
}
