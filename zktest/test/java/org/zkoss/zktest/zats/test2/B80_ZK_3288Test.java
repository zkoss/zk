/* B80_ZK_3288Test.java

	Purpose:
		
	Description:
		
	History:
		Thu, Sep 29, 2016  3:54:04 PM, Created by Sefi

Copyright (C)  Potix Corporation. All Rights Reserved.

This program is distributed under LGPL Version 2.1 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
package org.zkoss.zktest.zats.test2;

import junit.framework.Assert;
import org.junit.Test;
import org.zkoss.zktest.zats.WebDriverTestCase;
import org.zkoss.zktest.zats.ztl.JQuery;

/**
 * 
 * @author Sefi
 */
public class B80_ZK_3288Test extends WebDriverTestCase{
    @Test
    public void test () {
        connect();
        click(jq("@button:eq(0)"));
        waitResponse();
        JQuery center = jq("@center");
        int cntw = center.first().width();
        click(jq("@button:eq(1)"));
        waitResponse();
        Assert.assertEquals(cntw - JQuery.scrollbarWidth(), center.first().width());
        click(jq("@button:eq(2)"));
        waitResponse();
        Assert.assertEquals(cntw, center.first().width());
    }
}
