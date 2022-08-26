/* F80_ZK_2894Test.java

	Purpose:
		
	Description:
		
	History:
		Wed Nov 30 15:00:42 CST 2016, Created by jameschu

Copyright (C) 2016 Potix Corporation. All Rights Reserved.

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
public class F80_ZK_2894Test extends WebDriverTestCase{
    @Test
    public void test() {
        connect();
        click(jq("$nav1 .z-nav-content").get(0));
        waitResponse();
		assertEquals("open event", getZKLog());
		closeZKLog();
		waitResponse();
		click(jq("$nav2 .z-navitem-content"));
		waitResponse();
		assertEquals(false, isZKLogAvailable());
    }
}