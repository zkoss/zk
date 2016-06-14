/* F80_ZK_3176Test.java

	Purpose:
		
	Description:
		
	History:
		Mon, Jun 13, 2016  5:39:36 PM, Created by Sefi

Copyright (C)  Potix Corporation. All Rights Reserved.

This program is distributed under LGPL Version 2.1 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
package org.zkoss.zktest.zats.test2;
import org.junit.Assert;
import org.junit.Test;
import org.zkoss.zktest.zats.WebDriverTestCase;
import org.zkoss.zktest.zats.ztl.JQuery;

/**
 * 
 * @author Sefi
 */
public class F80_ZK_3176Test extends WebDriverTestCase{
    @Test
    public void test() {
        connect();
        JQuery menu = jq("@menu").eq(1);
        click(menu);
        waitResponse();
        JQuery menuPopup = jq("@menupopup");
        Assert.assertFalse(menuPopup.exists());

        click(jq("@button").eq(0));
        waitResponse();
        click(menu);
        waitResponse();
        menuPopup = jq("@menupopup");
        Assert.assertTrue(menuPopup.exists());
    }
}
