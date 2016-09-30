/* B80_ZK_3284Test.java

	Purpose:
		
	Description:
		
	History:
		Thu, Sep 29, 2016  3:41:52 PM, Created by Sefi

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
public class B80_ZK_3284Test extends WebDriverTestCase{
    @Test
    public void test() {
        connect();
        click(jq(".z-bandbox-button"));
        waitResponse();
        Assert.assertEquals("bandbox focused", jq("@label:eq(1)").text().trim());
    }
}
