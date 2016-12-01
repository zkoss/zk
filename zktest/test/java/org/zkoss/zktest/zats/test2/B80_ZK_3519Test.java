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

import org.junit.Test;

import org.zkoss.zktest.zats.WebDriverTestCase;
import org.zkoss.zktest.zats.ztl.JQuery;

import static org.junit.Assert.assertEquals;

/**
 * 
 * @author jameschu
 */
public class B80_ZK_3519Test extends WebDriverTestCase{
    @Test
    public void test () {
        connect();
        click(jq("@textbox:eq(0)"));
        waitResponse();
		assertEquals(false, isZKLogAvailable());
		rightClick(jq("@textbox:eq(0)"));
		waitResponse();
		assertEquals(false, isZKLogAvailable());
		click(jq("@decimalbox:eq(0)"));
		waitResponse();
		assertEquals(false, isZKLogAvailable());
		rightClick(jq("@decimalbox:eq(0)"));
		waitResponse();
		assertEquals(false, isZKLogAvailable());
		click(jq("@checkbox:eq(0)"));
		waitResponse();
		assertEquals(false, isZKLogAvailable());
		rightClick(jq("@checkbox:eq(0)"));
		waitResponse();
		assertEquals(false, isZKLogAvailable());
		click(jq("@button:eq(0)"));
		waitResponse();
		assertEquals(false, isZKLogAvailable());
		rightClick(jq("@button:eq(0)"));
		waitResponse();
		assertEquals(false, isZKLogAvailable());
    }


}
