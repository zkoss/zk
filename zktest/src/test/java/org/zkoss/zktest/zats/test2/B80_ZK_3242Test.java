/* B80_ZK_3242Test.java

	Purpose:
		
	Description:
		
	History:
		Fri, Jun 24, 2016  6:55:30 PM, Created by Sefi

Copyright (C)  Potix Corporation. All Rights Reserved.

This program is distributed under LGPL Version 2.1 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;

/**
 * 
 * @author Sefi
 */
public class B80_ZK_3242Test extends WebDriverTestCase{
    @Test
    public void test(){
        connect();
        click(jq("@button"));
        waitResponse();
        Assertions.assertEquals("false", getZKLog());
    }
}
