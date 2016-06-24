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

import junit.framework.Assert;
import org.junit.Test;
import org.zkoss.zktest.zats.WebDriverTestCase;

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
        Assert.assertEquals("false", getZKLog());
    }
}
