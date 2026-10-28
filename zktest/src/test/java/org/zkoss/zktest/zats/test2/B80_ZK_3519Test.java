/* B80_ZK_3519Test.java

	Purpose:
		
	Description:
		
	History:
		Wed Nov 30 10:11:40 CST 2016, Created by jameschu

Copyright (C)  Potix Corporation. All Rights Reserved.

This program is distributed under LGPL Version 2.1 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
package org.zkoss.zktest.zats.test2;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;



/**
 * 
 * @author jameschu
 */
public class B80_ZK_3519Test extends WebDriverTestCase{
    @Test
    public void test () {
        connect();
        clickAt(jq("@textbox:eq(0)"), 0, 0);
        waitResponse();
		assertEquals(false, isZKLogAvailable());
		rightClick(jq("@textbox:eq(0)"));
		waitResponse();
		assertEquals(false, isZKLogAvailable());
		clickAt(jq("@decimalbox:eq(0)"), 0, 0);
		waitResponse();
		assertEquals(false, isZKLogAvailable());
		rightClick(jq("@decimalbox:eq(0)"));
		waitResponse();
		assertEquals(false, isZKLogAvailable());
		clickAt(jq("@checkbox:eq(0)"), 0, 0);
		waitResponse();
		assertEquals(false, isZKLogAvailable());
		rightClick(jq("@checkbox:eq(0)"));
		waitResponse();
		assertEquals(false, isZKLogAvailable());
		clickAt(jq("@button:eq(0)"), 0, 0);
		waitResponse();
		assertEquals(false, isZKLogAvailable());
//		TODO: bug ZK-4979
//		rightClick(jq("@button:eq(0)"));
//		waitResponse();
//		assertEquals(false, isZKLogAvailable());
    }
}
